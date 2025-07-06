package dictionary

import java.nio.charset.Charset

abstract class Encoder {
    companion object {
        val shiftJIS: Charset = Charset.forName("Shift-JIS")

        val keycodeMap = mapOf(
            0xA3.toByte() to "1",
            0xA4.toByte() to "2",
            0xA5.toByte() to "3", 0xA6.toByte() to "4", 0xA7.toByte() to "5", 0xA8.toByte() to "6",
            0xA9.toByte() to "7", 0xAA.toByte() to "8",
            0xAB.toByte() to "9", 
            
            0xAC.toByte() to "A",
            0xAD.toByte() to "B",
            0xAE.toByte() to "C",
            0xAF.toByte() to "D",
            0xB0.toByte() to "E",
            0xB1.toByte() to "F", 
            0xB2.toByte() to "G", 
            0xB3.toByte() to "H",
            0xB4.toByte() to "I",
            0xB5.toByte() to "J",
            0xB6.toByte() to "K",
            0xB7.toByte() to "L",
            0xB8.toByte() to "M",
            0xB9.toByte() to "N",
            0xBA.toByte() to "O",
            0xBB.toByte() to "P",
            0xBC.toByte() to "Q",
            0xBD.toByte() to "R",
            0xBE.toByte() to "S",
            0xBF.toByte() to "T",
            0xC0.toByte() to "U",
            0xC1.toByte() to "V",
            0xC2.toByte() to "W",
            0xC3.toByte() to "X", 
            0xC4.toByte() to "Y", 
            0xC5.toByte() to "Z",

//            0xC5.toByte() to "a", 0xC6.toByte() to "b", 0xC7.toByte() to "c", 0xC8.toByte() to "d",
//            0xC9.toByte() to "e", 0xCA.toByte() to "f", 0xCB.toByte() to "g", 0xCC.toByte() to "h",
//            0xCD.toByte() to "i", 0xCE.toByte() to "j", 0xCF.toByte() to "k", 0xD0.toByte() to "l",
//            0xD1.toByte() to "m", 0xD2.toByte() to "n", 0xD3.toByte() to "o", 0xD4.toByte() to "p",
//            0xD5.toByte() to "q", 0xD6.toByte() to "r", 0xD7.toByte() to "s", 0xD8.toByte() to "t",
//            0xD9.toByte() to "u", 0xDA.toByte() to "v", 0xDB.toByte() to "w", 0xDC.toByte() to "x",
//            0xDD.toByte() to "y", 0xDE.toByte() to "z",

            0xDF.toByte() to "", 0xE0.toByte() to "", 0xE1.toByte() to "", 0xE2.toByte() to "",
            0xE3.toByte() to "!", 0xE4.toByte() to "_", 0xE5.toByte() to "", 0xE6.toByte() to "",
            0xE7.toByte() to "?", 0xE8.toByte() to "ー", 0xE9.toByte() to "%", 0xEA.toByte() to " ",
            0xEB.toByte() to "'", 0xEC.toByte() to "$", 0xED.toByte() to "#", 0xEE.toByte() to "@",
            0xEF.toByte() to ".", 0xF0.toByte() to ",", 0xF1.toByte() to "-",

            0x99.toByte() to "っ", // Double consonant marker (っ/ッ)
            0x01.toByte() to "k", 0x05.toByte() to "y", 0x06.toByte() to "sha", 0x08.toByte() to "shu",
            0x0A.toByte() to "s", 0x0F.toByte() to "ch", 0x10.toByte() to "n", 0x12.toByte() to "n",
            0x1E.toByte() to "m", 0x1F.toByte() to "g", 0x21.toByte() to "g", 0x24.toByte() to "j",
            0x32.toByte() to "j", 0x3A.toByte() to "r", 0x3C.toByte() to "r", 0x3E.toByte() to "f",
            0x45.toByte() to "t", 0x47.toByte() to "d",

            // Japanese kana mappings
            0x0B.toByte() to "tya", // ちゃ
            0x23.toByte() to "gyoi", // ぎょい
            0x28.toByte() to "zyoi", // じょい
            0x3F.toByte() to "huxe", // ふぇ
            0x51.toByte() to "a", // あ
            0x52.toByte() to "i", // い
            0x53.toByte() to "u", // う
            0x54.toByte() to "e", // え
            0x55.toByte() to "o", // お
            0x56.toByte() to "ka", // か
            0x57.toByte() to "ki", // き
            0x58.toByte() to "ku", // く
            0x59.toByte() to "ke", // け
            0x5A.toByte() to "ko", // こ
            0x5B.toByte() to "sa", // さ
            0x5C.toByte() to "si", // し
            0x5D.toByte() to "su", // す
            0x5E.toByte() to "se", // せ
            0x5F.toByte() to "so", // そ
            0x60.toByte() to "ta", // た
            0x61.toByte() to "ti", // ち
            0x62.toByte() to "tu", // つ
            0x63.toByte() to "te", // て
            0x64.toByte() to "to", // と
            0x65.toByte() to "na", // な
            0x66.toByte() to "ni", // に
            0x67.toByte() to "nu", // ぬ
            0x68.toByte() to "ne", // ね
            0x69.toByte() to "no", // の
            0x6A.toByte() to "ha", // は
            0x6B.toByte() to "hi", // ひ
            0x6C.toByte() to "hu", // ふ
            0x6D.toByte() to "he", // へ
            0x6E.toByte() to "ho", // ほ
            0x6F.toByte() to "ma", // ま
            0x70.toByte() to "mi", // み
            0x71.toByte() to "mu", // む
            0x72.toByte() to "me", // め
            0x73.toByte() to "mo", // も
            0x74.toByte() to "ya", // や
            0x75.toByte() to "yu", // ゆ
            0x76.toByte() to "yo", // よ
            0x77.toByte() to "ra", // ら
            0x78.toByte() to "ri", // り
            0x79.toByte() to "ru", // る
            0x7A.toByte() to "re", // れ
            0x7B.toByte() to "ro", // ろ
            0x7C.toByte() to "wa", // わ
            0x7D.toByte() to "wi", // ゐ (archaic)
            0x7E.toByte() to "ga", // が
            0x7F.toByte() to "gi", // ぎ
            0x80.toByte() to "gu", // ぐ
            0x81.toByte() to "ge", // げ
            0x82.toByte() to "go", // ご
            0x83.toByte() to "za", // ざ
            0x84.toByte() to "zi", // じ
            0x85.toByte() to "zu", // ず
            0x86.toByte() to "ze", // ぜ
            0x87.toByte() to "zo", // ぞ
            0x88.toByte() to "da", // だ
            0x89.toByte() to "di", // ぢ (archaic)
            0x8A.toByte() to "du", // づ (archaic)
            0x8B.toByte() to "de", // で
            0x8C.toByte() to "do", // ど
            0x8D.toByte() to "ba", // ば
            0x8E.toByte() to "bi", // び
            0x8F.toByte() to "bu", // ぶ
            0x90.toByte() to "be", // べ
            0x91.toByte() to "bo", // ぼ
            0x92.toByte() to "pa", // ぱ
            0x93.toByte() to "pi", // ぴ
            0x94.toByte() to "pu", // ぷ
            0x95.toByte() to "pe", // ぺ
            0x96.toByte() to "po", // ぽ
            0x97.toByte() to "nn" // ん
        )

        val reverseMap = keycodeMap.entries.associate { (key, value) -> value to key }
    }
}