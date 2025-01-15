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


    @Test
    public void testGetFilterOnAssignmentWithNullCursor() {
        String expected = NotionServiceFilters.FILTER_ON_ASSIGNMENT;
        assertEquals(expected, NotionServiceFilters.getFilterOnAssignment(null));
    }

    @Test
    public void testGetFilterOnAssignmentWithCursor() {
        String cursor = "abc123";
        String expected = String.format("""
                {
                    \"start_cursor\": \"%s\"
                    \"filter\": {
                        \"property\": \"Status\",
                        \"select\": {
                            \"equals\": \"On Assignment\"
                        }
                    }
                }
                """, cursor);
        assertEquals(expected, NotionServiceFilters.getFilterOnAssignment(cursor));
    }

    @Test
    public void testGetFilterDeveloperWithNullCursorAndNullFilter() {
        String expected = "{}";
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(null, null));
    }

    @Test
    public void testGetFilterDeveloperWithCursorAndNullFilter() {
        String cursor = "abc123";
        String expected = String.format("""
                {\"start_cursor\": \"%s\"}
                """, cursor);
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(cursor, null));
    }

    @Test
    public void testGetFilterDeveloperWithCursorAndFilter() {
        String cursor = "abc123";
        String filter = "In Progress";
        String expected = String.format("""
                {
                    \"start_cursor\": \"%s\"
                    \"filter\": {
                        \"property\": \"Status\",
                        \"select\": {
                            \"equals\": \"%s\"
                        }
                    }
                }
                """, cursor, filter);
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(cursor, filter));
    }

    @Test
    public void testGetFilterDeveloperWithNullCursorAndFilter() {
        String filter = "Completed";
        String expected = String.format("""
                {
                    \"filter\": {
                        \"property\": \"Status\",
                        \"select\": {
                            \"equals\": \"%s\"
                        }
                    }
                }
                """, filter);
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(null, filter));
    }
}
