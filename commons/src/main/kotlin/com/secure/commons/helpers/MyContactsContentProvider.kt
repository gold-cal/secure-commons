package com.secure.commons.helpers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.secure.commons.extensions.getIntValue
import com.secure.commons.extensions.getStringValue
import com.secure.commons.models.PhoneNumber
import com.secure.commons.models.SimpleContact

// used for sharing privately stored contacts in Simple Contacts with Simple Dialer, Simple SMS Messenger and Simple Calendar Pro
class MyContactsContentProvider {
    companion object {
        private const val AUTHORITY = "com.secure.commons.contactsprovider"
        val CONTACTS_CONTENT_URI = Uri.parse("content://$AUTHORITY/contacts")

        //const val FAVORITES_ONLY = "favorites_only"
        const val COL_RAW_ID = "raw_id"
        const val COL_CONTACT_ID = "contact_id"
        const val COL_NAME = "name"
        const val COL_PHOTO_URI = "photo_uri"
        const val COL_PHONE_NUMBERS = "phone_numbers"
        const val COL_BIRTHDAYS = "birthdays"
        const val COL_ANNIVERSARIES = "anniversaries"

        fun getSimpleContacts(context: Context, cursor: Cursor?): ArrayList<SimpleContact> {
            val contacts = ArrayList<SimpleContact>()
            val packageName = context.packageName.removeSuffix(".debug")
            if (packageName != "com.secure.smsmessenger" && packageName != "com.liturgical.calendar") {
                return contacts
            }

            try {
                cursor?.use {
                    if (cursor.moveToFirst()) {
                        do {
                            val rawId = cursor.getIntValue(COL_RAW_ID)
                            val contactId = cursor.getIntValue(COL_CONTACT_ID)
                            val name = cursor.getStringValue(COL_NAME)
                            val photoUri = cursor.getStringValue(COL_PHOTO_URI)
                            val phoneNumbersJson = cursor.getStringValue(COL_PHONE_NUMBERS)
                            val birthdaysJson = cursor.getStringValue(COL_BIRTHDAYS)
                            val anniversariesJson = cursor.getStringValue(COL_ANNIVERSARIES)

                            val phoneNumbersToken = object : TypeToken<ArrayList<PhoneNumber>>() {}.type
                            val phoneNumbers = Gson().fromJson<ArrayList<PhoneNumber>>(phoneNumbersJson, phoneNumbersToken) ?: ArrayList()

                            val stringsToken = object : TypeToken<ArrayList<String>>() {}.type
                            val birthdays = Gson().fromJson<ArrayList<String>>(birthdaysJson, stringsToken) ?: ArrayList()
                            val anniversaries = Gson().fromJson<ArrayList<String>>(anniversariesJson, stringsToken) ?: ArrayList()

                            val contact = SimpleContact(rawId, contactId, name, photoUri, phoneNumbers, birthdays, anniversaries)
                            contacts.add(contact)
                        } while (cursor.moveToNext())
                    }
                }
            } catch (ignored: Exception) {
            }
            return contacts
        }
    }
}
