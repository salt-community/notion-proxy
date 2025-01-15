package com.saltpgp.notionproxy.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotionServiceFiltersTest {

    @Test
    public void testFilterResponsiblePeople() {
        String expected = """
                {
                    \"filter\": {
                        \"property\": \"Guild\",
                        \"multi_select\": {
                            \"contains\": \"P&T\"
                        }
                    }
                }
                """;
        assertEquals(expected, NotionServiceFilters.FILTER_RESPONSIBLE_PEOPLE);
    }
}
