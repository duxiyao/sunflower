/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.utilities

import dagger.hilt.android.scopes.ServiceScoped
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//@ServiceScoped
class WOL constructor(private val macAddress:String,
                              private val targetIp:String,
                              private val port:Int) {

    @Throws(IOException::class)
    private fun wakeUp(macAddress: String, targetIp: String, port: Int) {
        val bytes = getMagicBytes(macAddress)
        //        InetAddress address = getMulticastAddress();
//        InetAddress address = InetAddress.getLocalHost();
//        InetAddress address = InetAddress.getByName("www.qq.com");
        val address: InetAddress = InetAddress.getByName(targetIp)
        System.out.println(address.getHostName())
        System.out.println(address.getHostAddress())
        send(bytes, address, port)
    }

    @Throws(IOException::class)
    private fun send(bytes: ByteArray, addr: InetAddress, port: Int) {
        val p = DatagramPacket(bytes, bytes.size, addr, port)
        DatagramSocket().send(p)
    }

    @Throws(UnknownHostException::class)
    private fun getMulticastAddress(): InetAddress? {
        return InetAddress.getByAddress(byteArrayOf(-1, -1, -1, -1))
    }

    @Throws(IOException::class)
    private fun getMagicBytes(macAddress: String): ByteArray {
        val bytes = ByteArrayOutputStream()
        for (i in 0..5) bytes.write(0xff)
        val macAddressBytes = parseHexString(macAddress)
        for (i in 0..15) bytes.write(macAddressBytes)
        bytes.flush()
        return bytes.toByteArray()
    }

    private fun parseHexString(string: String): ByteArray {
        val bytes = ByteArray(string.length / 2)
        var i = 0
        var j = 0
        while (i < string.length) {
            bytes[j] = string.substring(i, i + 2).toInt(16).toByte()
            i += 2
            j++
        }
        return bytes
    }

    @Throws(IOException::class)
    fun main(args: Array<String>) {
//        String macAddress = args[0];//54BF647ED56B
//        String targetIp = args[1];//115.171.85.136
//        String sport = args[2];//20000
        // String macAddress = "54BF647ED56B";
        // String targetIp = "115.171.85.136";
//        String targetIp = "www.qq.com";
        // String sport = "20000";
        val macAddress = "54BF647ED56B"
        val targetIp = "192.168.2.255"
        val sport = "9"
        val port = sport.toInt()
        wakeUp(macAddress, targetIp, port)
    }

    public fun wakeUp(){
        wakeUp(macAddress,targetIp,port)
        println("called wakeUp")
    }
}