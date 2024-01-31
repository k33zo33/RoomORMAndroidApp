package hr.k33zo.peoplemanager

import android.app.Application
import hr.k33zo.peoplemanager.dao.PeopleDatabase
import hr.k33zo.peoplemanager.dao.PersonDao

class App: Application() {
    private lateinit var personDao: PersonDao

    override fun onCreate() {
        super.onCreate()
        var db = PeopleDatabase.getInstance(this)
        personDao = db.personDao()
    }
    fun getPersonDao() = personDao
}