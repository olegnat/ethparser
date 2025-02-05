package pro.belbix.ethparser.web3.harvest.decoder;

import java.math.BigInteger;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import pro.belbix.ethparser.model.EthTransactionI;
import pro.belbix.ethparser.model.HarvestTx;
import pro.belbix.ethparser.web3.MethodDecoder;

@SuppressWarnings({"unchecked", "rawtypes"})
@Log4j2
public class VaultActionsLogDecoder extends MethodDecoder {

  public HarvestTx decode(Log ethLog) {
    if (!isValidLog(ethLog)) {
      return null;
    }
    String methodName;
    List<TypeReference<Type>> parameters;
    try {
      String methodId = parseMethodId(ethLog)
          .orElseThrow(() -> new IllegalStateException("Unknown topic " + ethLog));
      methodName = methodNamesByMethodId.get(methodId);
      parameters = findParameters(methodId)
          .orElseThrow(() -> new IllegalStateException("Not found parameters for " + methodId));
    } catch (IllegalStateException e) {
      log.warn("Wrong method for " + ethLog);
      return null;
    }
    List<Type> types = extractLogIndexedValues(ethLog.getTopics(), ethLog.getData(), parameters);
    HarvestTx tx = new HarvestTx();
    tx.setVault(new Address(ethLog.getAddress()));
    tx.setLogId(ethLog.getLogIndex().longValue());
    tx.setHash(ethLog.getTransactionHash());
    tx.setBlock(ethLog.getBlockNumber());
    tx.setBlockHash(ethLog.getBlockHash());
    tx.setMethodName(methodName);
    tx.setSuccess(true); //from logs always success
    enrich(types, methodName, tx);
    return tx;
  }

  private boolean isValidLog(Log log) {
    return log != null && !log.getTopics().isEmpty();
  }

    private void enrich(List<Type> types, String methodName, HarvestTx tx) {
        if (types == null) {
            return;
        }
        switch (methodName) {
            case "Deposit":
            case "Withdraw":
            case "Staked":
            case "Withdrawn":
            case "RewardPaid":
            case "StrategyAnnounced":
                tx.setOwner((String) types.get(0).getValue());
                tx.setAmount((BigInteger) types.get(1).getValue());
                return;
            case "Invest":
            case "RewardAdded":
            case "withdraw":
            case "stake":
                tx.setAmount((BigInteger) types.get(0).getValue());
                return;
            case "StrategyChanged":
            case "addVaultAndStrategy":
            case "OwnershipTransferred":
                tx.setAddressFromArgs1(new Address((String) types.get(0).getValue()));
                tx.setAddressFromArgs2(new Address((String) types.get(1).getValue()));
                return;
            case "Transfer":
            case "Approval":
                tx.setAddressFromArgs1(new Address((String) types.get(0).getValue()));
                tx.setAddressFromArgs2(new Address((String) types.get(1).getValue()));
                tx.setAmount((BigInteger) types.get(2).getValue());
                return;
            case "depositAll":
                tx.setIntFromArgs(parseInts(types.get(0)));
                tx.setAddressFromArgs(parseAddresses(types.get(1)));
                return;
            case "migrateInOneTx":
                Address[] addresses = new Address[4];
                addresses[0] = (Address) types.get(0).getValue();
                addresses[1] = (Address) types.get(1).getValue();
                addresses[2] = (Address) types.get(2).getValue();
                addresses[3] = (Address) types.get(3).getValue();
                tx.setAddressFromArgs(addresses);
                return;
            case "Staked#V2":
                tx.setOwner((String) types.get(0).getValue());
                BigInteger[] args = new BigInteger[5];
                args[0] = (BigInteger) types.get(1).getValue();
                args[1] = (BigInteger) types.get(2).getValue();
                args[2] = (BigInteger) types.get(3).getValue();
                args[3] = (BigInteger) types.get(4).getValue();
                args[4] = (BigInteger) types.get(5).getValue();
                tx.setIntFromArgs(args);
                return;
            case "Migrated":
                tx.setOwner((String) types.get(0).getValue());
                args = new BigInteger[2];
                args[0] = (BigInteger) types.get(1).getValue();
                args[1] = (BigInteger) types.get(2).getValue();
                tx.setIntFromArgs(args);
                return;
            case "exit":
            case "SmartContractRecorded":
            case "RewardDenied":
            case "RewardPaid#V2":
            case "RewardAdded#V2":
                return;
        }
        throw new IllegalStateException("Unknown method " + methodName);
    }

  @Override
  public EthTransactionI mapTypesToModel(List<Type> types, String methodID,
      Transaction transaction) {
    throw new UnsupportedOperationException();
  }
}
