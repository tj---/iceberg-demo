package com.foo.bar.trino;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Reading the Iceberg table via Trino (using Trino JDBC driver) 
 */
public class TrinoIcebergReader {

	public static void main(String[] args) throws Exception {
	    // Format: <Trino_Coordinator>/Catalog/Schema
	    try (Connection conn = DriverManager.getConnection("jdbc:trino://localhost:9080/iceberg/iceberg_gcs2?user=anything")) {
	        try(Statement stmt = conn.createStatement()) {
	            try(ResultSet rs = stmt.executeQuery("SELECT count(1) AS count_ FROM sample_table4 WHERE country = 'IN_IN'")) {
	                while (rs.next()) {
	                    Long count = rs.getLong("count_");
	                    System.out.println(String.format("Total rows for country = IN_IN are %s", count));
	                }
	            }
	        }
	    }
	}

}
