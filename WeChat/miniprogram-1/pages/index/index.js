// index.js
// 获取应用实例

const udp = wx.createUDPSocket();
var p = udp.bind(1234);
Page({
  data: {

  },
  light(e){
    var that = this
    var status = e.detail.value
    if (status==true){
      console.log("开灯")
      console.log(p)
      udp.send({
        address:"192.168.31.181",
        port:"8888",
        message:"1"
      })
    }
      else{
        console.log("关灯")
        console.log(p)
        udp.send({
          address:"192.168.31.181",
          port:"8888",
          message:"0"
        })
    }
  }
})
