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
import it.polito.mad.lab02.models.*

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
    fun DocumentSnapshot.toTimeslot(profile: Profile?): TimeSlot? {
        //if it is an adv of the loggedUser, the profile can be passed as empty Profile

        return try {
            val title = get("title") as String
            val description = get("description") as String
            val datetime = get("dateTime") as String
            val duration = get("duration") as String
            val location = get("location") as String
            val skill = get("skill")
            val user = get("user") as DocumentReference
            val assignee = get("assignee") as DocumentReference
            val state = get("state") as String


            val skillTmp = if (skill == null) {
                ""
            } else {
                (skill as DocumentReference).id
            }

            if (profile != null) {
                TimeSlot(
                    this.id,
                    title,
                    description,
                    datetime,
                    duration,
                    location,
                    skillTmp,
                    user.id,
                    profile,
                    assignee.id,
                    state
                )
            }
            else{
                throw Exception()
            }
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

    @JvmStatic
    fun DocumentSnapshot.toChat(publisher: Profile?, requester: Profile?, timeSlot: TimeSlot?): Chat? {

        return try {
            if (publisher != null && requester != null && timeSlot != null) {
                Chat(
                    publisher = publisher,
                    requester = requester,
                    timeSlot = timeSlot,
                    id = this.id
                )
            }
            else{
                throw Exception()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    @JvmStatic
    fun DocumentSnapshot.toMessage(user: Profile?): Message? {
        val text = get("text") as String
        val timestamp = get("timestamp") as Timestamp

        return try {
            if (user != null) {
                Message(
                    text = text,
                    timestamp = timestamp,
                    user = user,
                    id = this.id
                )
            }
            else{
                throw Exception()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

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
                rater.path,
                starsNum.toInt(),
                comment,
                timestamp.toString()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


}