package com.saltpgp.notionproxy.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotionServiceFiltersTest {

    @Test
    public void filterResponsiblePeople() {
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
    public void filterOnAssignment() {
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
    public void getFilterOnAssignmentWithNullCursor() {
        String expected = NotionServiceFilters.FILTER_ON_ASSIGNMENT;
        assertEquals(expected, NotionServiceFilters.getFilterOnAssignment(null));
    }

    @Test
    public void getFilterOnAssignmentWithCursor() {
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
    public void getFilterDeveloperWithNullCursorAndNullFilter() {
        String expected = """
                {
                }
                """;
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(null, null));
    }

    @Test
    public void getFilterDeveloperWithCursorAndNullFilter() {
        String cursor = "abc123";
        String expected = String.format("""
                {
                    \"start_cursor\": \"%s\"
                }
                """, cursor);
        assertEquals(expected, NotionServiceFilters.getFilterDeveloper(cursor, null));
    }

    @Test
    public void getFilterDeveloperWithCursorAndFilter() {
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
    public void getFilterDeveloperWithNullCursorAndFilter() {
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
