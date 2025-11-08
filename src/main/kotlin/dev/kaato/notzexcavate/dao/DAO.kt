package dev.kaato.notzexcavate.dao

import dev.kaato.notzexcavate.NotzExcavate.Companion.cf
import dev.kaato.notzexcavate.NotzExcavate.Companion.pathRaw
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DAO {
    companion object {
        val sql = if (cf.config.getBoolean("useMySQL"))
            arrayOf("""
                create table if not exists excavatormodel(
                id int primary key auto_increment,
                plotid varchar(36) unique not null,
                excavator blob not null)
            """.trimIndent(), """
                create table if not exists shovelmodel(
                id int primary key auto_increment,
                name varchar(36) unique not null,
                shovel blob not null)
            """.trimIndent())

        else arrayOf("""
            create table if not exists excavatormodel(
            id integer primary key autoincrement,
            plotid varchar(36) unique not null,
            excavator blob not null)
        """.trimIndent(), """
            create table if not exists shovelmodel(
            id integer primary key autoincrement,
            name varchar(36) unique not null,
            shovel blob not null)
        """.trimIndent())
    }

    private var c: Connection

    init {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:$pathRaw/notzexcavate.db")
            c.prepareStatement(sql[0]).use { it.execute() }
            c.prepareStatement(sql[1]).use { it.execute() }

        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    fun database(): Connection {
        return c
    }
}