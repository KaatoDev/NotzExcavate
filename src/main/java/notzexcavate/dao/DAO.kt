package notzexcavate.dao

import notzexcavate.Main.Companion.pathRaw
import notzexcavate.Main.Companion.sqlf
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DAO {
    private var c: Connection

    init {
        try {
            c = DriverManager.getConnection("jdbc:sqlite:$pathRaw/notzexcavator.db")

            sqlf.config.getString("sqlite").split(";").forEach {st ->
                c.prepareStatement(st).use { it.execute() }
            }
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    fun database(): Connection {
        return c
    }
}