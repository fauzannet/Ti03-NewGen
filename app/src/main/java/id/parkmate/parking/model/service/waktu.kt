package id.parkmate.parking.model.service

import java.util.*

class waktu {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val year = calendar.get(Calendar.YEAR)
    val jam = calendar.get(Calendar.HOUR_OF_DAY)
    val menit = calendar.get(Calendar.MINUTE)
    val detik = calendar.get(Calendar.SECOND)

    val jamString = String.format("%02d", jam)
    val menitString = String.format("%02d", menit)
    val detikString = String.format("%02d", detik)

    val waktuString = "$day/$month/$year,$jamString:$menitString:$detikString"
}