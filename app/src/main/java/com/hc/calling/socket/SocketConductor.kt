package com.hc.calling.socket

import android.content.Context
import com.google.gson.Gson
import com.hc.calling.callingtransaction.BuildConfig
import com.hc.calling.util.Commands2Server
import com.hc.calling.util.SystemUtil
import com.orhanobut.logger.Logger
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

/**
 * Created by ChanHong on 2019/3/20
 *
 */
class SocketConductor {


    var serverAddress = BuildConfig.SERVER_ADDRESS
    var emmiter: Emitter? = null
    var socket: Socket? = null
    var context: Context? = null

    companion object {
        val instance: SocketConductor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { SocketConductor() }
    }

    /**
     * build the connection between the c/s
     *  and post the phone information to server
     */
    fun connect2Server(context: Context): Emitter {

        val socket = IO.socket(serverAddress)
        this.context = context
        socket.connect()
        emmiter = socket.on(Socket.EVENT_CONNECT) {
            Logger.i("-----------connect_success-----------")
            val systemInfo = SystemInfo()
            systemInfo.IMEI = SystemUtil.getIMEI(context)
            systemInfo.brand = SystemUtil.getDeviceBrand()  //get system info
            systemInfo.model = SystemUtil.getSystemModel()
            systemInfo.version = SystemUtil.getSystemVersion()
            val data = Gson().toJson(systemInfo, SystemInfo::class.java)
            Logger.json(data)
            socket.emit(Commands2Server.SYSTEM_INFO, data)
        }
            .on(Socket.EVENT_DISCONNECT) {
                Logger.i("-----------connect_disconnected-----------")
            }


        this.socket = socket
        return emmiter!!
    }

}