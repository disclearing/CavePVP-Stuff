package net.frozenorb.foxtrot.server.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum Bundles {
    SOTW_BUNDLE("SOTW", Arrays.asList("mcrate give {player} Seasonal 3", "airdrops give {player} 5", "crates give {player} Seasonal 50", "cr givekey {player} Seasonal 100")),
    THREE("Seasonal", Arrays.asList("mcrate give {player} Seasonal 9", "airdrops give {player} 10", "crates give {player} Seasonal 100", "cr givekey {player} Seasonal 150")),
    SUPREME_BUNDLE("Supreme", Arrays.asList("airdrops give {player} 15", "crates give {player} Seasonal 120", "cr givekey {player} Seasonal 250", "cr givekey {player} Items 250", "randomownerkeys {player} 50"));

    @Getter String id;
    @Getter List<String> commands;
}
