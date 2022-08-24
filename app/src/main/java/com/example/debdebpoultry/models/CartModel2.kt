package com.example.debdebpoultry.models

import android.os.Parcel
import android.os.Parcelable


data class CartModel2(
    var id:Int, var prod_id:Int,
    var prod_name: String?, var img: String?,
    var total:Double, var tray:Int, val unit: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(prod_id)
        parcel.writeString(prod_name)
        parcel.writeString(img)
        parcel.writeDouble(total)
        parcel.writeInt(tray)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel2> {
        override fun createFromParcel(parcel: Parcel): CartModel2 {
            return CartModel2(parcel)
        }

        override fun newArray(size: Int): Array<CartModel2?> {
            return arrayOfNulls(size)
        }
    }
}

