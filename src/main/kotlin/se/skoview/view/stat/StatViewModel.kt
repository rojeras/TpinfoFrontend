/**
 * Copyright (C) 2013-2020 Lars Erik Röjerås
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package se.skoview.view.stat

import io.kvision.core.Col
import io.kvision.core.Color
import io.kvision.data.BaseDataComponent
import se.skoview.model.* // ktlint-disable no-wildcard-imports
import se.skoview.view.hippo.ItemType

class SInfoRecord(
    val itemType: ItemType,
    val itemId: Int,
    val description: String,
    val calls: Int
) {
    var color: Color = Color.name(Col.BLACK)
}

class SInfoList(private val itemType: ItemType) {

    val recordList = mutableListOf<SInfoRecord>()

    fun callList(): List<Int> {
        return recordList.map { it.calls }
    }

    // todo: Maybe this is the correct place to return the Google chart colors in the right order
    fun colorList(): List<Color> {
        return recordList.map { it.color }
    }

    fun descList(): List<String> {
        return recordList.map { it.description }
    }

    fun populate(ackMap: Map<Int, Int>, showSynonyms: Boolean) {
        // Will go via a temp collection
        val ackMapTmp = ackMap.toList().sortedBy { (_, value) -> value }.reversed().toMap()
        val callsTmp = mutableListOf<SInfoRecord>()

        var item: BaseItem?
        var desc: String
        for (entry in ackMapTmp) {
            when (this.itemType) {
                ItemType.CONSUMER -> {
                    item = ServiceComponent.mapp[entry.key]
                    desc =
                        if (showSynonyms && item!!.synonym != null) item.synonym.toString()
                        else "${item!!.description} (${item.hsaId})"
                }
                ItemType.PRODUCER -> {
                    item = ServiceComponent.mapp[entry.key]
                    desc =
                        if (showSynonyms && item!!.synonym != null) item.synonym.toString()
                        else "${item!!.description} (${item.hsaId})"
                }
                ItemType.LOGICAL_ADDRESS -> {
                    item = LogicalAddress.mapp[entry.key]
                    desc = "${item!!.description} (${item.name})"
                }
                ItemType.CONTRACT -> {
                    item = ServiceContract.mapp[entry.key]
                    desc =
                        if (showSynonyms && item!!.synonym != null) item.synonym.toString()
                        else item!!.description
                }
                else -> error("Unknown itemType in populate()!")
            }

            callsTmp.add(
                SInfoRecord(
                    this.itemType,
                    item.id,
                    desc,
                    ackMapTmp[entry.key] ?: error("ackMapTmp is null")
                )
            )
        }

        // Sort the recordList in reverse order based on number of calls
        callsTmp.sortBy { it.calls }
        callsTmp.reverse()

        // Populate the color fields
        val colorList = mkColorList(callsTmp.size)
        var colorIx = 0
        for (anItem in callsTmp) {
            anItem.color = colorList[colorIx]
            colorIx += 1
            if (colorIx > 30) colorIx = 0
        }
        // todo: Place to go through the sorted list and set the color

        this.recordList.clear()
        this.recordList.addAll(callsTmp)
    }
}

/**
 * The singleton for the data displayed in the view
 * It is updated through the view() method with an statisticsInfo object as parameter.
 */
object SInfo : BaseDataComponent() {
    var consumerSInfoList = SInfoList(ItemType.CONSUMER)
    var producerSInfoList = SInfoList(ItemType.PRODUCER)
    var logicalAddressSInfoList = SInfoList(ItemType.LOGICAL_ADDRESS)
    var contractSInfoList = SInfoList(ItemType.CONTRACT)

    fun createStatViewData(state: HippoState) {
        consumerSInfoList.populate(state.statBlob.callsConsumer, !state.showTechnicalTerms)
        producerSInfoList.populate(state.statBlob.callsProducer, !state.showTechnicalTerms)
        logicalAddressSInfoList.populate(state.statBlob.callsLogicalAddress, !state.showTechnicalTerms)
        contractSInfoList.populate(state.statBlob.callsContract, !state.showTechnicalTerms)
    }
}

fun mkColorList(size: Int): List<Color> {
    /* Google chart color scheme */
    val gColor: Array<Color> = arrayOf( // arrayOf<Color>()
        Color.hex("3366cc".toInt(radix = 16)),
        Color.hex("dc3912".toInt(radix = 16)),
        Color.hex("ff9900".toInt(radix = 16)),
        Color.hex("109618".toInt(radix = 16)),
        Color.hex("990099".toInt(radix = 16)),
        Color.hex("0099c6".toInt(radix = 16)),
        Color.hex("dd4477".toInt(radix = 16)),
        Color.hex("66aa00".toInt(radix = 16)),
        Color.hex("b82e2e".toInt(radix = 16)),
        Color.hex("316395".toInt(radix = 16)),
        Color.hex("994499".toInt(radix = 16)),
        Color.hex("22aa99".toInt(radix = 16)),
        Color.hex("aaaa11".toInt(radix = 16)),
        Color.hex("6633cc".toInt(radix = 16)),
        Color.hex("e67300".toInt(radix = 16)),
        Color.hex("8b0707".toInt(radix = 16)),
        Color.hex("651067".toInt(radix = 16)),
        Color.hex("329262".toInt(radix = 16)),
        Color.hex("5574a6".toInt(radix = 16)),
        Color.hex("3b3eac".toInt(radix = 16)),
        Color.hex("b77322".toInt(radix = 16)),
        Color.hex("16d620".toInt(radix = 16)),
        Color.hex("b91383".toInt(radix = 16)),
        Color.hex("f4359e".toInt(radix = 16)),
        Color.hex("9c5935".toInt(radix = 16)),
        Color.hex("a9c413".toInt(radix = 16)),
        Color.hex("2a778d".toInt(radix = 16)),
        Color.hex("668d1c".toInt(radix = 16)),
        Color.hex("bea413".toInt(radix = 16)),
        Color.hex("0c5922".toInt(radix = 16)),
        Color.hex("743411".toInt(radix = 16))
    )

    val colorList: MutableList<Color> = mutableListOf()
    var gColorIx = 0

    repeat(size) {
        colorList.add(gColor[gColorIx])
        gColorIx += 1
        if (gColorIx > 30) gColorIx = 0
    }

    return colorList
}
