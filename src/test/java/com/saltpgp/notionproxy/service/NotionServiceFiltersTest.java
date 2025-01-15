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

    @Test
    public void testFilterOnAssignment() {
        String expected = """
                {
                    \"filter\": {
                        \"property\": \"Status\",
                        \"select\": {
                            \"equals\": \"On Assignment\"
                        }
                    }
                }
                """;
        assertEquals(expected, NotionServiceFilters.FILTER_ON_ASSIGNMENT);
    }
}
