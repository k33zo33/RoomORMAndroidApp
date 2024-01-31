package hr.k33zo.peoplemanager.dao

enum class Role {
    PROFESSOR,
    STUDENT;

    companion object {
        fun getValuesAsArray(): Array<String> {
            return values().map { it.name }.toTypedArray()
        }
    }
}