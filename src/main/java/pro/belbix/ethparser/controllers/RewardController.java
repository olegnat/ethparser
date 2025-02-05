package pro.belbix.ethparser.controllers;

import static pro.belbix.ethparser.service.AbiProviderService.ETH_NETWORK;
import static pro.belbix.ethparser.utils.CommonUtils.parseLong;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.belbix.ethparser.dto.v0.RewardDTO;
import pro.belbix.ethparser.repositories.v0.RewardsRepository;

@ConditionalOnExpression("!${ethparser.onlyParse:false}")
@RestController
@Log4j2
public class RewardController {

    private final RewardsRepository rewardsRepository;

    public RewardController(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
    }

    @GetMapping(value = "/history/rewards/{pool}")
    List<RewardDTO> rewardsHistory(
        @PathVariable("pool") String pool,
        @RequestParam(value = "days", required = false) String days,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        int daysI = 7;
        if (days != null) {
            daysI = Integer.parseInt(days);
        }
        return rewardsRepository.fetchRewardsByVaultAfterBlockDate(
            pool,
            Instant.now().minus(daysI, ChronoUnit.DAYS).getEpochSecond(),
            Long.MAX_VALUE,
            network
        );
    }

    @RequestMapping(value = "api/transactions/last/reward", method = RequestMethod.GET)
    public List<RewardDTO> lastReward(
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        return rewardsRepository.fetchLastRewards(network);
    }

    @RequestMapping(value = "api/transactions/history/reward/{name}", method = RequestMethod.GET)
    public List<RewardDTO> historyReward(
        @PathVariable("name") String name,
        @RequestParam(value = "start", required = false) String start,
        @RequestParam(value = "end", required = false) String end,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
        ) {
        return rewardsRepository
            .getAllByVaultOrderByBlockDate(name, parseLong(start, 0),
                parseLong(end, Long.MAX_VALUE), network);
    }

    @RequestMapping(value = "api/transactions/history/reward", method = RequestMethod.GET)
    public List<RewardDTO> historyReward(
        @RequestParam(value = "start") String start,
        @RequestParam(value = "end", required = false) String end,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        if (end == null) {
            end = Long.MAX_VALUE + "";
        }
        return rewardsRepository
            .getAllOrderByBlockDate(parseLong(start, 0), parseLong(end, Long.MAX_VALUE), network);
    }

}
