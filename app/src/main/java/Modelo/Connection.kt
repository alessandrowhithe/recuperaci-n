package Modelo

import java.sql.Connection
import java.sql.DriverManager

class Connection {

    fun cadenaConexion(): Connection?{
        try {
            val ip = "jdbc:oracle:thin:@192.168.68.106:1521:xe"
            val usuario = "SYSTEM"
            val conta ="fakedrips"
            val connection=DriverManager.getConnection(ip,usuario,conta)
            return connection
        }catch (e: Exception){
            println("error:$e")
            return null
        }
    }
}