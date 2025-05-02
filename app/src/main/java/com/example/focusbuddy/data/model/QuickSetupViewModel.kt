package com.example.focusbuddy.data.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class QuickSetupViewModel : ViewModel() {
    var hasilAkhir: String = ""
    var nama: String = ""
    var usia: Int = 0
    var waktuPenggunaan: Int = 0
    var alasanPenggunaan: String = ""
    var terganggu: Boolean = false
    var alasanGangguan: String = ""
    var modePengawasan: String = ""
    var waktuPembatasan: Int = 0
    var daftarApp: List<String> = emptyList()
}

fun saveFirestore(viewModel: QuickSetupViewModel){
    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    if (uid==null){
        println("user not logged")
        return
    }

    val data = hashMapOf(
        "nama" to viewModel.nama,
        "usia" to viewModel.usia,
        "waktuPenggunaan" to viewModel.waktuPenggunaan,
        "alasanPenggunaan" to viewModel.alasanPenggunaan,
        "hasilAkhir" to viewModel.hasilAkhir,
        "terganggu" to viewModel.terganggu,
        "alasanGangguan" to viewModel.alasanGangguan,
        "modePengawasan" to viewModel.modePengawasan,
        "waktuPembatasan" to viewModel.waktuPembatasan,
    )

    db.collection("quickSetup")
        .document(uid)
        .set(data)
        .addOnSuccessListener {
            println("data saved")
        }
        .addOnFailureListener{ e ->
            println("data gagal disimpan")
        }

}