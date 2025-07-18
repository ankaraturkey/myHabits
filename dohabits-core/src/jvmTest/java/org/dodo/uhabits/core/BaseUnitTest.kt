/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Daily Loop Tracker.
 *
 * Daily Loop Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Daily Loop Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dodo.dohabits.core

import org.apache.commons.io.IOUtils
import com.dodo.dohabits.core.commands.CommandRunner
import com.dodo.dohabits.core.database.Database
import com.dodo.dohabits.core.database.DatabaseOpener
import com.dodo.dohabits.core.database.JdbcDatabase
import com.dodo.dohabits.core.database.MigrationHelper
import com.dodo.dohabits.core.models.HabitList
import com.dodo.dohabits.core.models.ModelFactory
import com.dodo.dohabits.core.models.Timestamp
import com.dodo.dohabits.core.models.memory.MemoryModelFactory
import com.dodo.dohabits.core.tasks.SingleThreadTaskRunner
import com.dodo.dohabits.core.test.HabitFixtures
import com.dodo.dohabits.core.utils.DateUtils.Companion.getStartOfTodayCalendar
import com.dodo.dohabits.core.utils.DateUtils.Companion.setFixedLocalTime
import com.dodo.dohabits.core.utils.DateUtils.Companion.setStartDayOffset
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.spy
import org.mockito.kotlin.validateMockitoUsage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths
import java.sql.DriverManager
import java.sql.SQLException

@RunWith(MockitoJUnitRunner::class)
open class BaseUnitTest {
    protected open lateinit var habitList: HabitList
    protected lateinit var fixtures: HabitFixtures
    protected lateinit var modelFactory: ModelFactory
    protected lateinit var taskRunner: SingleThreadTaskRunner
    protected open lateinit var commandRunner: CommandRunner
    protected var databaseOpener: DatabaseOpener = object : DatabaseOpener {
        override fun open(file: File): Database {
            return try {
                JdbcDatabase(
                    DriverManager.getConnection(
                        String.format(
                            "jdbc:sqlite:%s",
                            file.absolutePath
                        )
                    )
                )
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }

    @Before
    @Throws(Exception::class)
    open fun setUp() {
        setFixedLocalTime(FIXED_LOCAL_TIME)
        setStartDayOffset(0, 0)
        val memoryModelFactory = MemoryModelFactory()
        habitList = spy(memoryModelFactory.buildHabitList())
        fixtures = HabitFixtures(memoryModelFactory, habitList)
        modelFactory = memoryModelFactory
        taskRunner = SingleThreadTaskRunner()
        commandRunner = CommandRunner(taskRunner)
    }

    @After
    @Throws(Exception::class)
    open fun tearDown() {
        validateMockitoUsage()
        setFixedLocalTime(null)
        setStartDayOffset(0, 0)
    }

    fun unixTime(year: Int, month: Int, day: Int): Long {
        return unixTime(year, month, day, 0, 0)
    }

    open fun unixTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, milliseconds: Long = 0): Long {
        val cal = getStartOfTodayCalendar()
        cal.set(year, month, day, hour, minute)
        return cal.timeInMillis + milliseconds
    }

    fun timestamp(year: Int, month: Int, day: Int): Timestamp {
        return Timestamp(unixTime(year, month, day))
    }

    @Test
    fun nothing() {
    }

    @Throws(IOException::class)
    protected fun copyAssetToFile(assetPath: String, dst: File?) {
        IOUtils.copy(openAsset(assetPath), FileOutputStream(dst!!))
    }

    @Throws(IOException::class)
    protected fun openAsset(assetPath: String): InputStream {
        var inputStream = javaClass.getResourceAsStream(assetPath)
        if (inputStream != null) return inputStream
        val pwd = Paths.get(".").toAbsolutePath().normalize().toString()
        val fullPath = "$pwd/assets/test/$assetPath"
        val file = File(fullPath)
        if (file.exists() && file.canRead()) inputStream = FileInputStream(file)
        if (inputStream != null) return inputStream
        throw IllegalStateException("asset not found: $fullPath")
    }

    @Throws(IOException::class)
    protected fun openDatabaseResource(path: String): Database {
        val original = openAsset(path)
        val tmpDbFile = File.createTempFile("database", ".db")
        tmpDbFile.deleteOnExit()
        IOUtils.copy(original, FileOutputStream(tmpDbFile))
        return databaseOpener.open(tmpDbFile)
    }

    companion object {
        // 8:00am, January 25th, 2015 (UTC)
        const val FIXED_LOCAL_TIME = 1422172800000L
        fun buildMemoryDatabase(): Database {
            return try {
                val db: Database = JdbcDatabase(
                    DriverManager.getConnection("jdbc:sqlite::memory:")
                )
                db.execute("pragma user_version=8;")
                val helper = MigrationHelper(db)
                helper.migrateTo(DATABASE_VERSION)
                db
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }
}
