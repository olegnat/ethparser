package pro.belbix.ethparser.controllers;

import static pro.belbix.ethparser.service.AbiProviderService.ETH_NETWORK;
import static pro.belbix.ethparser.utils.CommonUtils.parseLong;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.ObjectMapperFactory;
import pro.belbix.ethparser.dto.v0.HardWorkDTO;
import pro.belbix.ethparser.model.PaginatedResponse;
import pro.belbix.ethparser.model.RestResponse;
import pro.belbix.ethparser.repositories.v0.HardWorkRepository;
import pro.belbix.ethparser.web3.harvest.HardWorkCalculator;

@ConditionalOnExpression("!${ethparser.onlyParse:false}")
@RestController
@Log4j2
public class HardWorkController {

    private final HardWorkRepository hardWorkRepository;
    private final HardWorkCalculator hardWorkCalculator;

    public HardWorkController(HardWorkRepository hardWorkRepository, HardWorkCalculator hardWorkCalculator) {
        this.hardWorkRepository = hardWorkRepository;
        this.hardWorkCalculator = hardWorkCalculator;
    }

    @RequestMapping(value = "api/transactions/last/hardwork", method = RequestMethod.GET)
    public List<HardWorkDTO> lastHardWorks(
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        return hardWorkRepository.fetchLatest(network);
    }

    @RequestMapping(value = "api/transactions/history/hardwork/{name}", method = RequestMethod.GET)
    public List<HardWorkDTO> historyHardWorkByName(
        @PathVariable("name") String name,
        @RequestParam(value = "start", required = false) String start,
        @RequestParam(value = "end", required = false) String end,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        return hardWorkRepository
            .findAllByVaultOrderByBlockDate(name, network, parseLong(start, 0),
                parseLong(end, Long.MAX_VALUE));
    }

    @RequestMapping(value = "api/transactions/history/hardwork", method = RequestMethod.GET)
    public List<HardWorkDTO> historyHardWork(
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        long fromL = 0L;
        long toL = Long.MAX_VALUE;
        if (from != null) {
            fromL = Long.parseLong(from);
        }
        if (to != null) {
            toL = Long.parseLong(to);
        }

        return hardWorkRepository.fetchAllInRange(fromL, toL, network);
    }

    @RequestMapping(value = "last_saved_gas_sum", method = RequestMethod.GET)
    public RestResponse lastSavedGasSum(
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        try {
            return RestResponse.ok((String.format("%.8f",
                hardWorkRepository.fetchLastGasSaved(network))));
        } catch (Exception e) {
            log.error("Error get last saved gas sum", e);
            return RestResponse.error("Server error during getting last saved gas");
        }

    }

    @RequestMapping(value = "total_saved_gas_fee_by_address", method = RequestMethod.GET)
    public RestResponse totalSavedGasFeeByEthAddress(
        @RequestParam(value = "address") String address,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        try {
            return RestResponse.ok((String.format("%.8f",
                hardWorkCalculator
                    .calculateTotalHardWorksFeeByOwner(address.toLowerCase(), network)
            )
            ));
        } catch (Exception e) {
            String msg = "Error get total saved gas fee for address: " + address;
            log.error(msg, e);
            return RestResponse.error(msg);
        }
    }

    @GetMapping(value = "/last/hardwork")
    public HardWorkDTO lastHardWork(
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        return hardWorkRepository.findFirstByNetworkOrderByBlockDateDesc(network);
    }

    @GetMapping(value = "/hardwork/pages")
    public RestResponse hardworkPages(
        @RequestParam("pageSize") String pageSize,
        @RequestParam("page") String page,
        @RequestParam(value = "ordering", required = false) String ordering,
        @RequestParam(value = "vault", required = false) String vault,
        @RequestParam(value = "minAmount", required = false) Integer minAmount,
        @RequestParam(value = "network", required = false, defaultValue = ETH_NETWORK) String network
    ) {
        try {
            int start = Integer.parseInt(page);
            int size = Integer.parseInt(pageSize);
            Sort sorting = Sort.by("blockDate");
            if (!Strings.isBlank(ordering) && "desc".equals(ordering)) {
                sorting = sorting.descending();
            }

            Page<HardWorkDTO> pages;
            if (minAmount == null) {
                minAmount = Integer.MIN_VALUE;
            }
            if (Strings.isBlank(vault)) {
                pages = hardWorkRepository
                    .fetchPages(minAmount, network, PageRequest.of(start, size, sorting));
            } else {
                pages = hardWorkRepository
                    .fetchPagesByVault(vault, network, minAmount,
                        PageRequest.of(start, size, sorting));
            }

            if (!pages.hasContent()) {
                return RestResponse.error("Data not found");
            }
            return RestResponse.ok(
                ObjectMapperFactory.getObjectMapper().writeValueAsString(
                    PaginatedResponse.builder()
                        .currentPage(start)
                        .previousPage(pages.hasPrevious() ? start - 1 : -1)
                        .nextPage(pages.hasNext() ? start + 1 : -1)
                        .totalPages(pages.getTotalPages())
                        .data(pages.getContent())
                        .build()
                )
            );

        } catch (Exception e) {
            String msg = "Error get hardwork pages";
            log.warn(msg, e);
            return RestResponse.error(msg);
        }
    }


}
