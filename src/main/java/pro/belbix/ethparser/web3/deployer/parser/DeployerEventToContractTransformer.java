package pro.belbix.ethparser.web3.deployer.parser;

import static pro.belbix.ethparser.web3.abi.FunctionsNames.LP_TOKEN;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.SYMBOL;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.TOKEN0;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.TOKEN1;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.UNDERLYING;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.USER_REWARD_PER_TOKEN_PAID;
import static pro.belbix.ethparser.web3.abi.FunctionsNames.VAULT_FRACTION_TO_INVEST_NUMERATOR;
import static pro.belbix.ethparser.web3.contracts.ContractConstants.ZERO_ADDRESS;
import static pro.belbix.ethparser.web3.contracts.ContractType.POOL;
import static pro.belbix.ethparser.web3.contracts.ContractType.UNKNOWN;
import static pro.belbix.ethparser.web3.contracts.ContractType.VAULT;
import static pro.belbix.ethparser.web3.deployer.decoder.DeployerActivityEnum.CONTRACT_CREATION;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pro.belbix.ethparser.dto.v0.DeployerDTO;
import pro.belbix.ethparser.web3.abi.FunctionsNames;
import pro.belbix.ethparser.web3.abi.FunctionsUtils;
import pro.belbix.ethparser.web3.contracts.ContractLoader;
import pro.belbix.ethparser.web3.contracts.ContractType;
import pro.belbix.ethparser.web3.contracts.ContractUpdater;
import pro.belbix.ethparser.web3.contracts.ContractUtils;
import pro.belbix.ethparser.web3.contracts.PlatformType;
import pro.belbix.ethparser.web3.contracts.models.LpContract;
import pro.belbix.ethparser.web3.contracts.models.PureEthContractInfo;
import pro.belbix.ethparser.web3.contracts.models.SimpleContract;
import pro.belbix.ethparser.web3.contracts.models.TokenContract;

@Service
@Log4j2
public class DeployerEventToContractTransformer {

  private final FunctionsUtils functionsUtils;
  private final ContractLoader contractLoader;
  private final ContractUpdater contractUpdater;

  public DeployerEventToContractTransformer(FunctionsUtils functionsUtils,
      ContractLoader contractLoader,
      ContractUpdater contractUpdater) {
    this.functionsUtils = functionsUtils;
    this.contractLoader = contractLoader;
    this.contractUpdater = contractUpdater;
  }

  public void handleAndSave(DeployerDTO dto) {
    var contracts = transform(dto);
    for (PureEthContractInfo contract : contracts) {
      if (ContractType.VAULT == contract.getContractType()) {
        contractLoader.loadVault((SimpleContract) contract,
            contract.getNetwork(), contract.getCreatedOnBlock());
      } else if (POOL == contract.getContractType()) {
        contractLoader.loadPool((SimpleContract) contract,
            contract.getNetwork(), contract.getCreatedOnBlock());
      } else if (ContractType.TOKEN == contract.getContractType()) {
        contractLoader.loadToken((TokenContract) contract,
            contract.getNetwork(), contract.getCreatedOnBlock());
      } else if (ContractType.UNI_PAIR == contract.getContractType()) {
        contractLoader.loadUniPair((LpContract) contract,
            contract.getNetwork(), contract.getCreatedOnBlock());
      }
    }

    // need to link token to lp after loading
    for (PureEthContractInfo contract : contracts) {
      if (ContractType.TOKEN == contract.getContractType()) {
        contractLoader.linkUniPairsToToken((TokenContract) contract, contract.getNetwork());
      }
    }

    if (!contracts.isEmpty()) {
      contractUpdater.updateContracts();
    }
  }


  public List<PureEthContractInfo> transform(DeployerDTO dto) {
    if (!isEligible(dto)) {
      return List.of();
    }

    ContractType type = detectContractType(dto);
    // detect only vaults and pools
    if (VAULT != type && POOL != type) {
      return List.of();
    }

    String address = dto.getToAddress();
    long block = dto.getBlock();
    String network = dto.getNetwork();

    SimpleContract contract = new SimpleContract(
        (int) dto.getBlock(),
        nameForContract(address, block, network, type),
        dto.getToAddress());
    contract.setContractType(type);
    return List.of(contract);
  }

  private boolean isEligible(DeployerDTO dto) {
    if (dto == null) {
      return false;
    }
    if (isVaultInit(dto.getMethodName())) {
      return true;
    }
    if (!CONTRACT_CREATION.name().equals(dto.getType())) {
      return false;
    }
    return true;
  }

  private boolean isVaultInit(String methodName) {
    return "0x8fc1708c".equalsIgnoreCase(methodName)
        || "initializeVault".equals(methodName);
  }

  private String nameForContract(String address, long block, String network, ContractType type) {
    if (POOL == type) {
      address = functionsUtils.callAddressByName(
          LP_TOKEN, address, block, network)
          .orElseThrow(
              () -> new IllegalStateException("Can't fetch vault for pool")
          ); // use only vault address for name creation
    }
    String underlyingAddress = functionsUtils.callAddressByName(
        UNDERLYING, address, block, network)
        .orElseThrow();
    String underlyingName = functionsUtils.callStrByName(
        FunctionsNames.NAME, underlyingAddress, block, network)
        .orElse("");
    PlatformType platformType = detectPlatformType(underlyingName);
    String tokenNames = tokenNames(underlyingAddress, block, network);
    String prefix;
    String name;
    // single token or something new
    if (platformType.isUnknown()) {
      if (tokenNames.isBlank()) {
        String underlyingSymbol = underlyingSymbol(underlyingAddress, block, network);
        if (underlyingSymbol.isBlank()) {
          // better than nothing
          if (underlyingName.isBlank()) {
            name = functionsUtils.callAddressByName(
                SYMBOL, address, block, network)
                .orElse("UNKNOWN_NAME");
          } else {
            name = underlyingName.replaceAll(" ", "");
          }
        } else {
          // SINGLE TOKEN
          name = underlyingSymbol;
        }
      } else {
        // UNKNOWN LP UNDERLYING
        name = underlyingName.replaceAll(" ", "") + "_" + tokenNames;
      }
    } else {
      prefix = platformType.getPrettyName();
      if (tokenNames.isBlank()) {
        String underlyingSymbol = underlyingSymbol(underlyingAddress, block, network);
        name = prefix + "_" + underlyingSymbol;
      } else {
        // LP UNDERLYING
        name = prefix + "_" + tokenNames;
      }
    }

    if (name.endsWith("_")) {
      name = name.substring(0, name.length() - 1);
    }
    if (type == POOL) {
      name = "ST_" + name;
    }
    return name;
  }

  private String underlyingSymbol(String address, long block, String network) {
    return functionsUtils.callStrByName(
        FunctionsNames.SYMBOL, address, block, network)
        .orElse("")
        .replaceAll("/", "_")
        .replaceAll("\\+", "_")
        .replaceAll("Crv", "")
        .toUpperCase();
  }

  private ContractType detectContractType(DeployerDTO dto) {
    if (functionsUtils.callIntByName(
        VAULT_FRACTION_TO_INVEST_NUMERATOR,
        dto.getToAddress(),
        dto.getBlock(), dto.getNetwork()).isPresent()) {
      return VAULT;
    } else if (functionsUtils.callIntByNameWithAddressArg(
        USER_REWARD_PER_TOKEN_PAID,
        dto.getToAddress(), // any address
        dto.getToAddress(),
        dto.getBlock(), dto.getNetwork()).isPresent()) {
      return POOL;
    }
    return UNKNOWN;
  }

  private PlatformType detectPlatformType(String name) {
    if (name.startsWith("Curve")) {
      return PlatformType.CURVE;
    } else if (name.startsWith("Uniswap")) {
      return PlatformType.UNISWAP;
    } else if (name.startsWith("SushiSwap")) {
      return PlatformType.SUSHISWAP;
    } else if (name.startsWith("1inch")) {
      return PlatformType.ONEINCH;
    }
    return PlatformType.UNKNOWN;
  }

  private String tokenNames(String address, long block, String network) {
    String token0Adr = functionsUtils.callAddressByName(
        TOKEN0, address, block, network)
        .orElse(null);
    if (token0Adr == null) {
      return "";
    }
    String token1Adr = functionsUtils.callAddressByName(
        TOKEN1, address, block, network)
        .orElse(null);
    if (token1Adr == null) {
      return "";
    }

    String token0Name;
    if (ZERO_ADDRESS.equalsIgnoreCase(token0Adr)) {
      token0Name = ContractUtils.getBaseNetworkWrappedTokenName(network);
    } else {
      token0Name = functionsUtils.callStrByName(
          FunctionsNames.SYMBOL, token0Adr, block, network)
          .orElse("");
    }

    String token1Name;
    if (ZERO_ADDRESS.equalsIgnoreCase(token1Adr)) {
      token1Name = ContractUtils.getBaseNetworkWrappedTokenName(network);
    } else {
      token1Name = functionsUtils.callStrByName(
          FunctionsNames.SYMBOL, token1Adr, block, network)
          .orElse("");
    }
    return token0Name + "_" + token1Name;
  }

}