package com.atos.genericpattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sap.me.common.RouterType;
import com.sap.me.frame.SystemBase;

public class PatternDaoMnanagerImpl {

	private Connection getConnection() {
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}

	private final SystemBase dbBase = SystemBase
			.createSystemBase("jdbc/jts/wipPool");

	public boolean createPattern(Pattern pattern) {
		String insertTableSQL = "INSERT INTO Z_PATTERN (PATTERN, PATTERN_TYPE, PATTERN_TYPE_MASTER, PATTERN_TYPE_VALUE,CREATED_DATE_TIME,CREATED_BY,HANDLE) VALUES (?,?,?,?,?,?,?)";
		Connection con = null;
		boolean isCreated = false;
		PreparedStatement preparedStatement = null;
		try {

			con = getConnection();
			preparedStatement = con.prepareStatement(insertTableSQL);

			preparedStatement.setString(1, pattern.getPatternName());
			preparedStatement.setString(2, pattern.getPatternType());
			preparedStatement.setString(3, pattern.getPatternTypeMaster());
			preparedStatement.setString(4, pattern.getPatternTypeValue());
			preparedStatement.setDate(5, pattern.getCreatedDate());
			preparedStatement.setString(6, pattern.getCreatedBy());
			preparedStatement.setString(7, pattern.getHandle());
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			isCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isCreated = false;
		} finally {
			try {
				if (preparedStatement != null) {

					preparedStatement.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return isCreated;

	}

	public boolean createPatternSequence(PatternSequence patternSequence) {
		String insertTableSQL = "INSERT INTO Z_PATTERN_SEQUENCE (PATTERN_BO, PATTERN_ATTRIBUTE, PATTERN_VALUE, CURRENT_VERSION,CREATED_DATE_TIME,CREATED_BY,HANDLE,SEQUENCE_NO,ATTRIBUTE_VALUE_TYPE) VALUES (?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		boolean isCreated = false;
		PreparedStatement preparedStatement = null;
		try {

			con = getConnection();
			preparedStatement = con.prepareStatement(insertTableSQL);

			preparedStatement.setString(1, patternSequence.getPatternBo());
			preparedStatement.setString(2, patternSequence
					.getPatternAttribute());
			preparedStatement.setString(3, patternSequence.getPatternValue());
			preparedStatement.setString(4, patternSequence.getCurrentVersion());
			preparedStatement.setDate(5, patternSequence.getCreatedOn());
			preparedStatement.setString(6, patternSequence.getCreatedBy());
			preparedStatement.setString(7, patternSequence.getHandle());
			preparedStatement.setInt(8, patternSequence.getSequenceNo());
			preparedStatement.setString(9, patternSequence.getPatternValueType());
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
			isCreated = true;
		} catch (SQLException e) {
			e.printStackTrace();
			isCreated = false;
		} finally {
			try {
				if (preparedStatement != null) {

					preparedStatement.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return isCreated;

	}

	public Pattern findPattern(String patternName) {
		Connection con = null;
		String query = "select PATTERN, PATTERN_TYPE, PATTERN_TYPE_MASTER, PATTERN_TYPE_VALUE,CREATED_DATE_TIME,CREATED_BY,HANDLE from  Z_PATTERN  WHERE PATTERN = ?";
		PreparedStatement ps = null;
		Pattern pattern = null;
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, patternName);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				pattern = new Pattern();
				pattern.setCreatedBy(rs.getString("CREATED_BY"));
				pattern.setCreatedDate(rs.getDate("CREATED_DATE_TIME"));
				pattern.setPatternName(rs.getString("PATTERN"));
				pattern.setPatternType(rs.getString("PATTERN_TYPE"));
				pattern.setPatternTypeMaster(rs
						.getString("PATTERN_TYPE_MASTER"));
				pattern.setPatternTypeValue(rs.getString("PATTERN_TYPE_VALUE"));
				pattern.setHandle(rs.getString("HANDLE"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return pattern;

	}

	public List<PatternSequence> findPatternSequence(String patternBO) {
		Connection con = null;
		String query = "select PATTERN_BO, PATTERN_ATTRIBUTE, PATTERN_VALUE, CURRENT_VERSION,CREATED_DATE_TIME,CREATED_BY,HANDLE,SEQUENCE_NO from  Z_PATTERN_SEQUENCE  WHERE PATTERN_BO = ?";
		PreparedStatement ps = null;
		List<PatternSequence> patternSequences = new ArrayList<PatternSequence>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ps.setString(1, patternBO);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				PatternSequence patternSequence = new PatternSequence();
				patternSequence.setCreatedBy(rs.getString("CREATED_BY"));
				patternSequence.setCreatedOn(rs.getDate("CREATED_DATE_TIME"));
				patternSequence.setPatternBo(rs.getString("PATTERN_BO"));
				patternSequence.setPatternAttribute(rs
						.getString("PATTERN_ATTRIBUTE"));
				patternSequence.setPatternValue(rs.getString("PATTERN_VALUE"));
				patternSequence.setCurrentVersion(rs
						.getString("CURRENT_VERSION"));
				patternSequence.setHandle(rs.getString("HANDLE"));
				patternSequence.setSequenceNo(rs.getInt("SEQUENCE_NO"));
				patternSequences.add(patternSequence);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patternSequences;

	}

	public List<PatternItem> findAllPatterns(String pattern) {
		Connection con = null;
		String query = "select  DISTINCT PATTERN from  Z_PATTERN ";
		if (!StringUtils.isBlank(pattern)) {
			query = query + " where PATTERN like '" + pattern + "%'";
		}
		PreparedStatement ps = null;
		List<PatternItem> patterns = new ArrayList<PatternItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PatternItem patternItem = new PatternItem();
				patternItem.setValue("" + rs.getString("PATTERN"));
				patterns.add(patternItem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patterns;
	}

	public void deletePattern(Pattern pattern) {

		String query1 = "delete from Z_PATTERN_SEQUENCE where PATTERN_BO = ?";
		String query2 = "delete from Z_PATTERN where HANDLE = ?";
		Connection con = null;
		PreparedStatement ps = null;
		try {

			con = getConnection();
			ps = con.prepareStatement(query1);
			ps.setString(1, pattern.getHandle());

			// execute delete SQL stetement
			ps.executeUpdate();
			ps = con.prepareStatement(query2);
			ps.setString(1, pattern.getHandle());

			// execute delete SQL stetement
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

	}

	public List<PatternItem> findAllBOM(String bom) {
		Connection con = null;
		String query = "select  BOM,HANDLE from  BOM ";
		if (!StringUtils.isBlank(bom)) {
			query = query + " where BOM like '" + bom + "%'";
		}
		PreparedStatement ps = null;
		List<PatternItem> patterns = new ArrayList<PatternItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PatternItem patternItem = new PatternItem();
				patternItem.setValue("" + rs.getString("BOM"));
				patternItem.setVersion("" + rs.getString("HANDLE"));
				patterns.add(patternItem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patterns;
	}

	public List<PatternItem> findAllBOMComponent(String bomComponent) {
		Connection con = null;
		String query = "select   ITEM from  ITEM inner join BOM_COMPONENT on ITEM.HANDLE where BOM_COMPONENT ";
		if (!StringUtils.isBlank(bomComponent)) {
			query = query + " where BOM like '" + bomComponent + "%'";
		}
		PreparedStatement ps = null;
		List<PatternItem> patterns = new ArrayList<PatternItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PatternItem patternItem = new PatternItem();
				patternItem.setValue("" + rs.getString("BOM"));
				patterns.add(patternItem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patterns;
	}

	public List<PatternItem> findAllRouter(String router, String site) {
		Connection con = null;
		String query = "select  ROUTER , ROUTER_TYPE , REVISION , HANDLE from  ROUTER where SITE = '"
				+ site + "'";
		if (!StringUtils.isBlank(router)) {
			query = query + " and ROUTER like '" + router + "%'";
		}
		PreparedStatement ps = null;
		List<PatternItem> patterns = new ArrayList<PatternItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PatternItem patternItem = new PatternItem();
				RouterRef routerRef = new RouterRef();
				routerRef.setRouter("" + rs.getString("ROUTER"));
				routerRef.setRevision("" + rs.getString("REVISION"));
				if (rs.getString("ROUTER_TYPE") != null)
					routerRef.setRouterType(RouterType.fromValue(rs
							.getString("ROUTER_TYPE")));
				routerRef.setHandle("" + rs.getString("HANDLE"));
				patternItem.setRouterRef(routerRef);
				patternItem.setValue(rs.getString("ROUTER"));
				patterns.add(patternItem);
			}

		} catch (IllegalArgumentException exp) {
			exp.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patterns;
	}

	public List<PatternItem> findAllRoutingSteps(String handle,
			String routingtep) {
		Connection con = null;
		String query = "select  DESCRIPTION from  ROUTER_STEP where ROUTER_BO = '"
				+ handle + "'";
		if (!StringUtils.isBlank(routingtep)) {
			query = query + " and DESCRIPTION like '" + routingtep + "%'";
		}
		PreparedStatement ps = null;
		List<PatternItem> patterns = new ArrayList<PatternItem>();
		try {

			con = getConnection();
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PatternItem patternItem = new PatternItem();
				patternItem.setValue(rs.getString("DESCRIPTION"));
				patterns.add(patternItem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {

					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return patterns;
	}

}
