package pro.belbix.ethparser.dto.v0;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.belbix.ethparser.dto.DtoI;

@Entity
@Table(name = "hard_work", indexes = {
    @Index(name = "idx_hard_work", columnList = "blockDate"),
    @Index(name = "idx_hard_work_vault", columnList = "vault"),
    @Index(name = "idx_hard_work_2", columnList = "fullRewardUsd"),
    @Index(name = "idx_hard_work_network", columnList = "network")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardWorkDTO implements DtoI {

  @Id
  private String id;
  private String vault;
  private long block;
  private long blockDate;
  private String network;
  // don't use it, share price doesn't change for AutoStake strats
  // keep for compatibility and statistic
  private double shareChange;
  /**
   * strategy + ps sum of earns
   */
  private double fullRewardUsd;
  private double fullRewardUsdTotal;
  private double tvl;
  private double allProfit;
  private long periodOfWork;
  private long psPeriodOfWork;
  private double perc;
  private double apr;
  private double weeklyProfit;
  /**
   * {@link #fullRewardUsd} sum of all vaults for the last week
   */
  private double weeklyAllProfit;
  private double psTvlUsd;
  private double psApr;
  private double farmBuyback;
  private double farmBuybackSum;
  private int callsQuantity;
  private int poolUsers;
  private double savedGasFees;
  private double savedGasFeesSum;
  private double fee;
  private Double weeklyAverageTvl;
  private double farmBuybackEth;
  private double feeEth;
  private double gasUsed;
  private long idleTime;
  private double invested;
  private double investmentTarget;
  private double farmPrice;
  private double ethPrice;
  private Double profitSharingRate;
  private Double buyBackRate;
  private Integer autoStake;

  public String print() {
    return Instant.ofEpochSecond(blockDate) + " "
        + vault + " "
        + fullRewardUsd + " "
        + fullRewardUsdTotal + " "
        + id;

  }
}
