package it.polito.mad.lab02

data class ProfileClass(val fullName: String,
                        val nickname: String,
                        val email: String,
                        val location: String,
                        val skills: String,
                        val description: String) {
    override fun toString(): String {
        return "$fullName - $nickname"
    }
}