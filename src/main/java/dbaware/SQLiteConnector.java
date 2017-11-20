package dbaware;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class SQLiteConnector implements IdbConnector {
	private static volatile SQLiteConnector instance;
	private static String dbname = "test.db";
	// SQLite connection string
	private static String url = "jdbc:sqlite:" + dbname;

	public static SQLiteConnector getInstance() {
		if (instance == null)
			synchronized (SQLiteConnector.class) {
				if (instance == null)
					instance = new SQLiteConnector();
			}
		return instance;
	}

	/**
	 * Connect to the test.db database
	 *
	 * @return the Connection object
	 * @throws Exception
	 */
	private static Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection(url);

		try (Statement stmt = conn.createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS mLexems (\n"
					+ "	lx_id integer PRIMARY KEY, lex text NOT NULL);";
			stmt.execute(sql);
			sql = "CREATE TABLE IF NOT EXISTS mDictionary (\n"
					+ "	w_id integer PRIMARY KEY, lx_id integer, rword text NOT NULL);";
			stmt.execute(sql);
			sql = "CREATE TABLE IF NOT EXISTS mPhrases (\n"
					+ "	ph_id integer PRIMARY KEY, created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";
			stmt.execute(sql);
			sql = "CREATE TABLE IF NOT EXISTS mPhraseContent (\n"
					+ "	с_id integer PRIMARY KEY, ph_id integer, w_id integer);";
			stmt.execute(sql);
		} catch (SQLException e) {
			throw e;
		}

		return conn;
	}

	@Override
	public long SaveLex(String word) {
		long result = GetWord(word);
		if (result == -1) {
			String sql = "INSERT INTO mLexems(lx_id, lex) VALUES(NULL, ?)";
			try (Connection conn = connect()) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, word);
				pstmt.executeUpdate();

				sql = "SELECT last_insert_rowid()";
				pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					result = rs.getLong(1);
				}
				pstmt.close();
				pstmt = null;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public long GetWord(String rword) {
		long result = -1;
		String sql = "SELECT lx_id FROM mLexems WHERE upper(lex) = ?";
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, rword.toUpperCase());
			ResultSet rs = pstmt.executeQuery();
			// Читаем только первую запись
			if (rs.next())
				result = rs.getLong(1);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	@Override
	public long SavePhrase(long ph_id) {
		String sql = "INSERT INTO mPhrases(ph_id) VALUES(NULL)";
		if (ph_id == -1)
			try (Connection conn = connect()) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();

				sql = "SELECT last_insert_rowid()";
				pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					ph_id = rs.getLong(1);
				}
				pstmt.close();
				pstmt = null;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		else
			// Пока тут нечего UPDATE
			try (Connection conn = connect();
					PreparedStatement pstmt = conn.prepareStatement(sql)) {
				// pstmt.setInt(1, ph_id);
				pstmt.setNull(1, java.sql.Types.INTEGER);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		return ph_id;
	}

	@Override
	public long SavePhraseContent(long ph_id, long w_id) {
		long result = -1;
		String sql = "INSERT INTO mPhraseContent(с_id, ph_id, w_id) VALUES(NULL, ?, ?)";
		try (Connection conn = connect()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, ph_id);
			pstmt.setLong(2, w_id);
			pstmt.executeUpdate();

			sql = "SELECT last_insert_rowid()";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result = rs.getLong(1);
			}
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	/**
	 * select all rows in the mPhrases table
	 */
	public void selectAll() {
		String sql = "SELECT с_id, ph_id, w_id FROM mPhraseContent";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {
				System.out.println(rs.getInt("с_id") + "\t"
						+ rs.getString("ph_id") + "\t" + rs.getInt("w_id"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		SQLiteConnector dbConnector = SQLiteConnector.getInstance();
		/*long w1 = dbConnector.SaveLex("qq");
		long w2 = dbConnector.SaveLex("ww");
		long ph_id = dbConnector.SavePhrase(-1);
		long x = dbConnector.SavePhraseContent(ph_id, w1);
		System.out.println(x);
		x = dbConnector.SavePhraseContent(ph_id, w2);
		System.out.println(x);
		*/
		//long x = dbConnector.GetWord("qq");
		//System.out.println(x);
		dbConnector.selectAll();
		// dbConnector.selectRegimByGroupID(2);
	}

}
