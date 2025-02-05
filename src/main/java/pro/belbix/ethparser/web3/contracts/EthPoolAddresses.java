package pro.belbix.ethparser.web3.contracts;

import static pro.belbix.ethparser.web3.contracts.Contract.createContracts;

import java.util.List;

class EthPoolAddresses {

  private EthPoolAddresses() {
  }

  //todo collect info from deployer
  static final List<Contract> POOLS = createContracts(
      new Contract(11041842, "ST_UNI_ETH_DAI", "0x7aeb36e22e60397098C2a5C51f0A5fB06e7b859c"),
      new Contract(11041835, "ST_UNI_ETH_USDC", "0x156733b89Ac5C704F3217FEe2949A9D4A73764b5"),
      new Contract(11023309, "ST_UNI_ETH_USDT", "0x75071F2653fBC902EBaff908d4c68712a5d1C960"),
      new Contract(11041845, "ST_UNI_ETH_WBTC", "0xF1181A71CC331958AE2cA2aAD0784Acfc436CB93"),
      new Contract(11087149, "ST_WETH", "0x3DA9D911301f8144bdF5c3c67886e5373DCdff8e"),
      new Contract(11087160, "ST_USDC", "0x4F7c28cCb0F1Dbd1388209C67eEc234273C878Bd"),
      new Contract(11087164, "ST_USDT", "0x6ac4a7ab91e6fd098e13b7d347c6d4d1494994a2"),
      new Contract(11087156, "ST_DAI", "0x15d3A64B2d5ab9E152F16593Cdebc4bB165B5B4A"),
      new Contract(11068190, "ST_WBTC", "0x917d6480Ec60cBddd6CbD0C8EA317Bcc709EA77B"),
      new Contract(11087172, "ST_RENBTC", "0x7b8Ff8884590f44e10Ea8105730fe637Ce0cb4F6"),
      new Contract(11087179, "ST_CRVRENWBTC", "0xA3Cf8D1CEe996253FAD1F8e3d68BDCba7B3A3Db5"),
      new Contract(11043768, "ST_SUSHI_WBTC_TBTC", "0x9523FdC055F503F73FF40D7F66850F409D80EF34"),
      new Contract(11153147, "ST_YCRV", "0x6D1b6Ea108AA03c6993d8010690264BA96D349A8"),
      new Contract(11159034, "ST_3CRV", "0x27F12d1a08454402175b9F0b53769783578Be7d9"),
      new Contract(10997827, "ST_TUSD", "0xeC56a21CF0D7FeB93C25587C12bFfe094aa0eCdA"),
      new Contract(10957879, "ST_PS", "0x8f5adC58b32D4e5Ca02EAC0E293D35855999436C"),
      new Contract(10797544, "PS_V0", "0x59258F4e15A5fC74A7284055A8094F58108dbD4f"),
      new Contract(11231016, "ST_CRV_TBTC", "0x017eC1772A45d2cf68c429A820eF374f0662C57c"),
      new Contract(11269974, "ST_SUSHI_ETH_DAI", "0x76Aef359a33C02338902aCA543f37de4b01BA1FA"),
      new Contract(11269986, "ST_SUSHI_ETH_USDC", "0x6B4e1E0656Dd38F36c318b077134487B9b0cf7a6"),
      new Contract(11269989, "ST_SUSHI_ETH_USDT", "0xA56522BCA0A09f57B85C52c0Cc8Ba1B5eDbc64ef"),
      new Contract(11269980, "ST_SUSHI_ETH_WBTC", "0xE2D9FAe95f1e68afca7907dFb36143781f917194"),
      new Contract(11374201, "ST_IDX_ETH_DPI", "0xad91695b4bec2798829ac7a4797e226c78f22abd"),
      new Contract(11257885, "ST_CRV_CMPND", "0xC0f51a979e762202e9BeF0f62b07F600d0697DE1"),
      new Contract(11257902, "ST_CRV_BUSD", "0x093C2ae5E6F3D2A897459aa24551289D462449AD"),
      new Contract(11257895, "ST_CRV_USDN", "0xef4Da1CE3f487DA2Ed0BE23173F76274E0D47579"),
      new Contract(11380850, "ST_CRV_HUSD", "0x72C50e6FD8cC5506E166c273b6E814342Aa0a3c1"),
      new Contract(11380837, "ST_CRV_HBTC", "0x01f9CAaD0f9255b0C0Aa2fBD1c1aA06ad8Af7254"),
      new Contract(10777093, "ST_UNI_LP_USDC_FARM", "0x99b0d6641A63Ce173E6EB063b3d3AED9A35Cf9bf"),
      new Contract(11407437, "ST_UNI_LP_WETH_FARM", "0x6555c79a8829b793F332f1535B0eFB1fE4C11958"),
      new Contract(11407202, "ST_UNI_LP_GRAIN_FARM", "0xe58f0d2956628921cdEd2eA6B195Fc821c3a2b16"),
      new Contract(11609483, "ST_UNI_BAC_DAI", "0x797F1171DC5001B7A79ff7Dca68c9539329ccE48"),
      new Contract(11616168, "ST_UNI_DAI_BAS", "0xf330891f02F8182D7D4e1A962ED0F3086D626020"),
      new Contract(11609503, "ST_SUSHI_MIC_USDT", "0x98Ba5E432933E2D536e24A2C021deBb8404588C1"),
      new Contract(11609508, "ST_SUSHI_MIS_USDT", "0xf4784d07136b5b10c6223134E8B36E1DC190725b"),
      new Contract(11641089, "ST_CRV_OBTC", "0x91B5cD52fDE8dbAC37C95ECafEF0a70bA4c182fC"),
      new Contract(11647955, "ST_ONEINCH_ETH_DAI", "0xda5e9706274d88bbf1bd1877a9b462fc40cdcfad"),
      new Contract(11648033, "ST_ONEINCH_ETH_USDC", "0x9a9a6148f7b0a9767ac1fdc67343d1e3e219fddf"),
      new Contract(11648090, "ST_ONEINCH_ETH_USDT", "0x2a80e0b572e7ef61ef81ef05ec024e1e52bd70bd"),
      new Contract(11648101, "ST_ONEINCH_ETH_WBTC", "0x747318cf3171d4e2a1a52bbd3fcc9f9c690448b4"),
      new Contract(11660866, "ST_DAI_BSG", "0xf5b221E1d9C3a094Fb6847bC3E241152772BbbF8"),
      new Contract(11660882, "ST_DAI_BSGS", "0x63e7D3F6e208ccE4967b7a0E6A50A397Af0b0E7A"),
      new Contract(11655673, "ST_BAC", "0x3cddE34C96eCB95A1232c9673e23f2dB5fA72280"),
      new Contract(11655679, "ST_ESD", "0xDB9C2EbA87301e6951d6FBE7a458832eaab2137E"),
      new Contract(11655689, "ST_DSD", "0x7c497298d9576499e17F9564CE4E13faa87A9b33"),
      new Contract(11674656, "ST_CRV_EURS", "0xf4d50f60D53a230abc8268c6697972CB255Cd940"),
      new Contract(11680930, "ST_CRV_UST", "0xDdb5D3CCd968Df64Ce48b577776BdC29ebD3120e"),
      new Contract(11681748, "ST_MAAPL_UST", "0xc02d1Da469d68Adc651Dd135d1A7f6b42F4d1A57"),
      new Contract(11681037, "ST_MAMZN_UST", "0x8Dc427Cbcc75cAe58dD4f386979Eba6662f5C158"),
      new Contract(11681054, "ST_MGOOGL_UST", "0xfE83a00DF3A98dE218c08719FAF7e3741b220D0D"),
      new Contract(11681067, "ST_MTSLA_UST", "0x40C34B0E1bb6984810E17474c6B0Bcc6A6B46614"),
      new Contract(11686133, "ST_CRV_STETH", "0x2E25800957742C52b4d69b65F9C67aBc5ccbffe6"),
      new Contract(11745429, "ST_CRV_GUSD", "0x538613A19Eb84D86a4CcfcB63548244A52Ab0B68"),
      new Contract(11830965, "ST_CRV_AAVE", "0x10f1fc85eAA1F064e38EEffDa82fBa414841f438"),
      new Contract(11777533, "ST_SUSHI_SUSHI_ETH", "0x16fBb193f99827C92A4CC22EFe8eD7390465BFa3"),
      new Contract(11924852, "ST_UNI_WBTC_KBTC", "0xdD496A6Ba1B4Cf2b3ef42dEf132e2B2c570941FE"),
      new Contract(11925210, "ST_UNI_WBTC_KLON", "0x719d70457658358f2e785B38307CfE24071b7417"),
      new Contract(11953917, "ST_MNFLX_UST", "0x937D4b84f139bec548b825FdCE33B172C5Bf755a"),
      new Contract(11954014, "ST_MTWTR_UST", "0x677AD66025063bE55B070685E618a84FF3dd62be"),
      new Contract(11954061, "ST_SUSHI_ETH_UST", "0x59eeb34065dB1621c68d26f37ffEFf3A89E5FA8b"),
      new Contract(11954328, "ST_CRV_LINK", "0x9c6FbDBF59808CD920fDb166c25E2E9FcF708dD1"),
      new Contract(11954222, "ST_SUSHI_HODL", "0xf550804Ebd6f89CdC9EC8E92CE8DE91A2F64a82E"),
      new Contract(11999019, "ST_ETH_DAI_HODL", "0xF5833723b150929D1Fddf785ED9D92eEe722387d"),
      new Contract(11999015, "ST_ETH_USDC_HODL", "0x378C314028071C92efE15d6990B6cf93594fCB9D"),
      new Contract(11998997, "ST_ETH_USDT_HODL", "0x0c67FBa277A3FE1B0a792ef5bc798cBbDA15a7f5"),
      new Contract(11999022, "ST_ETH_WBTC_HODL", "0x08aA65118996eaa61372B65978Cfa684F2C749b2"),
      new Contract(12044252, "ST_MUSE_ETH", "0x743BD82331CAe227Fa2c8c97f345A6846f8383b1"),
      new Contract(12044258, "ST_DUDES20_ETH", "0x3B808A7d8CCdF8893d1360ff421beF4440376842"),
      new Contract(12044260, "ST_MASK20_ETH", "0xC5fc56779b5925218D2Cdac093d0bFc6de7Cc2D1"),
      new Contract(12044261, "ST_ROPE20_ETH", "0x14ac1BDdd9160866590C6c4ec16853A1510845b9"),
      new Contract(12135577, "ST_MCAT20_ETH", "0xE7E1C3624188052a2367B63048a32A7429980113"),
      new Contract(12226558, "ST_MEME20_ETH", "0x269FA8c40062692CFD5494e5ec7daD64745b45af"),
      new Contract(12226476, "ST_GPUNK20_ETH", "0x9b36b44C6E3BfB1aDfbe31BB7b8c4f9AF7A804Ee"),
      new Contract(12226577, "ST_MVI_ETH", "0x079158BECA3c0ee6aE44B43357c6317E339DdC69"),
      new Contract(12226915, "ST_KXUSD_DAI", "0x8e54bB5e1411Be9c776b17B0cD267F2955377E32"),
      new Contract(12063572, "ST_ONEINCH_ONEINCH_WBTC", "0xd8a3C7d1dEcCB8445a4391F6052E5a0726f2F270"),
      new Contract(12063524, "ST_ONEINCH_ONEINCH_USDC", "0x516658d83A68747C34FD5aeCba7068ad4bD4783d"),
      new Contract(11905294, "ST_ONEINCH_ETH_ONEINCH", "0x16b5089ED717409849b2748AC73adFbfE7ec0301"),
      new Contract(12016907, "ST_CRV_USDP", "0x15AEB9B209FEC67c672dBF5113827daB0b80f390")
  );

}
