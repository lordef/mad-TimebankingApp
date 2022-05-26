package it.polito.mad.lab02.viewmodels

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.lab02.models.Profile
import it.polito.mad.lab02.models.Rating
import it.polito.mad.lab02.models.Skill
import it.polito.mad.lab02.models.TimeSlot
import it.polito.mad.lab02.viewmodels.ViewmodelsUtils.toSkill

object ViewmodelsUtils {
    @JvmStatic
    fun DocumentSnapshot.toProfile(): Profile? {
        return try {
            val imageUri = get("imageUri") as String
            val fullName = get("fullName") as String
            val nickname = get("nickname") as String
            val email = get("email") as String
            val location = get("location") as String
            val skills = get("skills") as List<DocumentReference>
            val description = get("description") as String
            val uid = get("uid") as String
            val tmpList = skills.map { s -> s.path.split("/").last() }

            Profile(imageUri, fullName, nickname, email, location, tmpList, description, uid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun DocumentSnapshot.toTimeslot(profile: Profile): TimeSlot? {
        //if it is an adv of the loggedUser, the profile can be passed as empty Profile

        return try {
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("dateTime") as String
            val duration = get("duration") as String
            val location = get("location") as String
            val skill = get("skill")
            val user = get("user") as DocumentReference

            val skillTmp = if (skill == null) {
                ""
            } else {
                (skill as DocumentReference).path.split("/").last()
            }

            TimeSlot(
                this.id,
                title,
                description,
                datetime,
                duration,
                location,
                skillTmp,
                user.path,
                profile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @JvmStatic
    fun DocumentSnapshot.toSkill(): Skill? {
        return try {
            val name = get("name") as String
            val occurrences = get("occurrences") as Number
            Skill(this.reference.path, name, occurrences)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @JvmStatic
    fun QuerySnapshot.toSkillList(): List<Skill>? {
        val listTmp: MutableList<Skill> = mutableListOf()
        for (s in this.documents) {
            try {
                val skill = s.toSkill()
                listTmp.add(skill!!)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return listTmp
    }


    //TODO
    @JvmStatic
    fun DocumentSnapshot.toStarsNumber(): Int? {
        return try {
            val starsNum = get("starsNum") as Number
            starsNum.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @JvmStatic
    fun DocumentSnapshot.toRating(): Rating? {
        //if it is an adv of the loggedUser, the profile can be passed as empty Profile

        return try {
            val rated = get("rated") as DocumentReference
            val rater = get("rater") as DocumentReference
            val starsNum = get("starsNum") as Number
            val comment = get("comment") as String //TODO: check if empty
            val timestamp = get("timestamp") as Timestamp //Timestamp on FB
            //TODO: useful example of code?
//            val skillTmp = if (skill == null) {
//                ""
//            } else {
//                (skill as DocumentReference).path.split("/").last()
//            }

            Rating(
                "",
                rated.toString(),
                rater.toString(),
                starsNum.toInt(),
                comment,
                timestamp.toString()
            )
//            Rating("", "rr", "rd", 2, "cmt", "ts")

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


}