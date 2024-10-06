package com.taxi.taxist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class FakeData {
    private val testDriversDatabase = ArrayList<DriverInfo>()

    fun getDrivers(context: Context): ArrayList<DriverInfo> {
        val sedan = context.getString(R.string.sedan)
        val hatchback = context.getString(R.string.hatchback)
        val crossover = context.getString(R.string.crossover)
        val coupe = context.getString(R.string.coupe)
        val station = context.getString(R.string.station_wagon)

        val mens = getMenDrivers(context)
        val women = getWomenDrivers(context)

        testDriversDatabase.add(
            DriverInfo(
                "James",
                39,
                25,
                15,
                4.4f,
                "Toyota Camry 2024",
                sedan,
                4,
                "+1-000-000-0000",
                mens[0].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "John",
                39,
                24,
                14,
                4.8f,
                "Honda Accord 2023",
                sedan,
                7,
                "+1-000-000-0000",
                mens[25].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Robert",
                55,
                35,
                29,
                4.1f,
                "Ford Mustang 2022",
                coupe,
                3,
                "+1-000-000-0000",
                mens[2].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Michael",
                35,
                24,
                14,
                3.6f,
                "Chevrolet Malibu 2023",
                sedan,
                2,
                "+1-000-000-0000",
                mens[22].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "William",
                39,
                29,
                19,
                4.2f,
                "Nissan Altima 2023",
                sedan,
                5,
                "+1-000-000-0000",
                mens[6].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Charlotte",
                39,
                29,
                19,
                4.9f,
                "Toyota Venza 2024",
                station,
                4,
                "+1-000-000-0000",
                women[0].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Mary",
                35,
                24,
                15,
                4.2f,
                "Hyundai Sonata 2024",
                sedan,
                2,
                "+1-000-000-0000",
                women[4].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "David",
                34,
                24,
                15,
                3.7f,
                "Subaru Legacy 2023",
                sedan,
                1,
                "+1-000-000-0000",
                mens[23].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Alexander",
                35,
                29,
                19,
                4.8f,
                "Mercedes-Benz E-Class Wagon 2024",
                station,
                8,
                "+1-000-000-0000",
                mens[19].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Richard",
                49,
                34,
                24,
                4.7f,
                "Mazda 6 2023",
                sedan,
                6,
                "+1-000-000-0000",
                mens[24].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Charles",
                34,
                20,
                14,
                4.6f,
                "Kia K5 2024",
                sedan,
                4,
                "+1-000-000-0000",
                mens[20].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Thomas",
                44,
                29,
                19,
                5.0f,
                "Volkswagen Passat 2022",
                station,
                5,
                "+1-000-000-0000",
                mens[21].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Daniel",
                49,
                34,
                24,
                4.4f,
                "Jeep Cherokee 2023",
                crossover,
                4,
                "+1-000-000-0000",
                null
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Matthew",
                55,
                39,
                25,
                3.7f,
                "Toyota RAV4 2024",
                crossover,
                6,
                "+1-000-000-0000",
                mens[12].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Mason",
                55,
                39,
                25,
                4.6f,
                "Honda Civic Hatchback 2023",
                hatchback,
                9,
                "+1-000-000-0000",
                mens[11].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Anthony",
                49,
                34,
                24,
                4.9f,
                "Honda CR-V 2023",
                crossover,
                11,
                "+1-000-000-0000",
                mens[9].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Jennifer",
                35,
                20,
                15,
                4.2f,
                "Mini Cooper 2023",
                hatchback,
                1,
                "+1-000-000-0000",
                women[2].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Patricia",
                29,
                19,
                14,
                4.0f,
                "Fiat 500 2024",
                hatchback,
                2,
                "+1-000-000-0000",
                women[1].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Jackson",
                39,
                29,
                19,
                4.5f,
                "Volvo V60 2023",
                station,
                5,
                "+1-000-000-0000",
                mens[7].asImageBitmap()

            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Mark",
                50,
                29,
                19,
                4.1f,
                "Hyundai Tucson 2024",
                crossover,
                7,
                "+1-000-000-0000",
                mens[5].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Donald",
                55,
                35,
                24,
                3.9f,
                "Subaru Forester 2023",
                crossover,
                5,
                "+1-000-000-0000",
                mens[6].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Amelia",
                30,
                20,
                14,
                4.7f,
                "Nissan Versa Note 2023",
                hatchback,
                9,
                "+1-000-000-0000",
                women[3].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Steven",
                50,
                34,
                24,
                5.0f,
                "Mazda CX-5 2024",
                crossover,
                14,
                "+1-000-000-0000",
                mens[4].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Paul",
                45,
                30,
                19,
                4.2f,
                "Kia Sportage 2023",
                crossover,
                3,
                "+1-000-000-0000",
                mens[3].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Aria",
                45,
                30,
                19,
                4.7f,
                "Hyundai Ioniq 5 Wagon 2024",
                station,
                2,
                "+1-000-000-0000",
                women[5].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "John",
                49,
                29,
                24,
                4.9f,
                "Chevrolet Camaro 2023",
                coupe,
                8,
                "+1-000-000-0000",
                mens[1].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Andrew",
                60,
                35,
                25,
                4.3f,
                "Volkswagen Tiguan 2023",
                crossover,
                7,
                "+1-000-000-0000",
                mens[18].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Robert",
                59,
                44,
                34,
                4.7f,
                "BMW M4 2023",
                coupe,
                11,
                "+1-000-000-0000",
                mens[2].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Linda",
                34,
                24,
                19,
                4.8f,
                "Mercedes-Benz A-Class 2024",
                sedan,
                5,
                "+1-000-000-0000",
                women[6].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Joshua",
                39,
                29,
                19,
                4.0f,
                "Ford Escape 2023",
                crossover,
                3,
                "+1-000-000-0000",
                mens[10].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Brian",
                44,
                34,
                24,
                4.1f,
                "Chevrolet Equinox 2023",
                crossover,
                7,
                "+1-000-000-0000",
                mens[16].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Ethan",
                39,
                29,
                19,
                4.9f,
                "Audi A5 2023",
                coupe,
                8,
                "+1-000-000-0000",
                mens[15].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Liam",
                69,
                49,
                29,
                5.0f,
                "Mercedes-Benz C-Class Coupe 2024",
                coupe,
                15,
                "+1-000-000-0000",
                mens[6].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Lucas",
                39,
                24,
                14,
                4.3f,
                "Volkswagen Golf 2024",
                hatchback,
                1,
                "+1-000-000-0000",
                mens[13].asImageBitmap()
            )
        )
        testDriversDatabase.add(
            DriverInfo(
                "Levi",
                35,
                25,
                15,
                4.1f,
                "Toyota Corolla Hatchback 2024",
                hatchback,
                4,
                "+1-000-000-0000",
                mens[8].asImageBitmap()
            )
        )

        return testDriversDatabase
    }

    private fun getMenDrivers(context: Context): ArrayList<Bitmap> {
        val listOfDrivers = ArrayList<Bitmap>()

        val resources = context.resources
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_1))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_2))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_3))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_4))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_5))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_6))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_7))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_8))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_9))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_10))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_11))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_12))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_13))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_14))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_15))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_16))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_17))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_18))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_19))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_20))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_21))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_22))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_23))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_24))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_25))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.men_26))

        return listOfDrivers
    }

    private fun getWomenDrivers(context: Context): ArrayList<Bitmap> {
        val listOfDrivers = ArrayList<Bitmap>()

        val resources = context.resources
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_1))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_2))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_3))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_4))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_5))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_6))
        listOfDrivers.add(BitmapFactory.decodeResource(resources, R.drawable.woman_7))

        return listOfDrivers
    }

    private fun getCompressedBitmapJPEG(image: Bitmap): Bitmap {
        val out = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 10, out)
        return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
    }

    private fun getCoordinates() {
        val lanStatuaZL = 49.809763
        val lonStatuaZL = 24.897144

        val lanMoaHataZL = 49.810020
        val lonSMoaHataZL = 24.891731

        val lanCenterLviv = 49.844511
        val lonCenterLviv = 24.025904

        val lanHresnaLviv = 49.817250
        val lonHresnaLviv = 24.072008
    }
}