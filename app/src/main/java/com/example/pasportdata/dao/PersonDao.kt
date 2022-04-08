package com.example.pasportdata.dao

import androidx.room.*
import com.example.pasportdata.entity.Person
import java.io.Serializable

@Dao
interface PersonDao {
    @Insert
    fun addPerson(person: Person)

    @Delete
    fun deletePerson(person: Person)

    @Update
    fun editPerson(person: Person)

    @Query("select * from person")
    fun getAllPerson(): List<Person>

    @Query("select * from person where id = :a")
    fun getPersonById(a: Int): Person

}