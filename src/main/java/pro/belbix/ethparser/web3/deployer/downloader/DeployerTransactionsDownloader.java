package pro.belbix.ethparser.web3.deployer.downloader;
import static pro.belbix.ethparser.web3.contracts.ContractConstants.ETH_BLOCK_NUMBER_30_AUGUST_2020;

import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;
import pro.belbix.ethparser.dto.v0.DeployerDTO;
import pro.belbix.ethparser.properties.AppProperties;
import pro.belbix.ethparser.web3.Web3Subscriber;
import pro.belbix.ethparser.web3.deployer.db.DeployerDbService;
import pro.belbix.ethparser.web3.deployer.parser.DeployerTransactionsParser;

@Service
@Log4j2
public class DeployerTransactionsDownloader {

  private final Web3Subscriber web3Subscriber;
  private final DeployerDbService deployerDbService;
  private final DeployerTransactionsParser parser;
  private final AppProperties appProperties;
  private final BlockingQueue<Transaction> transactionQueue = new ArrayBlockingQueue<>(100);

  @Value("${deployer-download.from:}")
  private Integer from;

  @Value("${deployer-download.to:}")
  private Integer to;

  public DeployerTransactionsDownloader(
      Web3Subscriber web3Subscriber, DeployerDbService deployerDbService,
      DeployerTransactionsParser parser,
      AppProperties appProperties) {
    this.web3Subscriber = web3Subscriber;
    this.deployerDbService = deployerDbService;
    this.parser = parser;
    this.appProperties = appProperties;
  }

  public void start() {
    DefaultBlockParameter blockFrom;
    DefaultBlockParameter blockTo;

    if (from == null) {
      blockFrom = ETH_BLOCK_NUMBER_30_AUGUST_2020;
    } else {
      blockFrom = DefaultBlockParameter.valueOf(new BigInteger(from.toString()));
    }

    if (to == null) {
      blockTo = DefaultBlockParameterName.LATEST;
    } else {
      blockTo = DefaultBlockParameter.valueOf(new BigInteger(to.toString()));
    }

    log.info("DeployerTransactionsDownloader start");
    parse(blockFrom, blockTo);
  }

  private void parse(DefaultBlockParameter start, DefaultBlockParameter end) {
    Disposable subscription =
        web3Subscriber.getTransactionFlowableRangeSubscription(
            transactionQueue, start, end, appProperties.getNetwork());
    while (!subscription.isDisposed()) {
      Transaction transaction = null;
      try {
        transaction = transactionQueue.poll(1, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {
      }
      DeployerDTO dto = parser.parseDeployerTransaction(transaction, appProperties.getNetwork());
      if (dto != null) {
        try {
          deployerDbService.save(dto);
        } catch (Exception e) {
          log.error("Can't save " + dto.toString(), e);
          break;
        }
      }
    }
  }

  public void setFrom(Integer from) {
    this.from = from;
  }

  public void setTo(Integer to) {
    this.to = to;
  }
}
