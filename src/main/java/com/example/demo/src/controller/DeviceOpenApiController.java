package com.example.demo.src.controller;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@Slf4j
public class DeviceOpenApiController {


    @PostMapping("/device/analysis/healthData")
    public JSONObject  healthData(@RequestBody JSONObject jsonObject){
        log.info("接收第三方健康数据推送：{}",jsonObject.toJSONString());
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("msg","操作成功");
        return result;
    }

    @PostMapping("/device/analysis/deviceLocation")
    public JSONObject  deviceLocation(@RequestBody JSONObject jsonObject){
        log.info("接收第三方位置数据推送：{}",jsonObject.toJSONString());
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("msg","操作成功");
        return result;
    }

    @PostMapping("/device/analysis/stepsData")
    public JSONObject  stepsData(@RequestBody JSONObject jsonObject){
        log.info("接收第三方计步数据推送：{}",jsonObject.toJSONString());
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("msg","操作成功");
        return result;
    }

    @PostMapping("/device/analysis/alarmData")
    public JSONObject  alarmData(@RequestBody JSONObject jsonObject){
        log.info("接收设备上报报警数据推送：{}",jsonObject.toJSONString());
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("msg","操作成功");
        return result;
    }

    @PostMapping("/device/pull/deviceLocation")
    public Object  pullDeviceLocation(@RequestBody JSONObject jsonObject){
        String url = "https://openapi.miwitracker.com/api/command/sendcommand";
        String accessToken = getAccessToken();

        JSONObject param = new JSONObject();
        param.put("AccessToken",accessToken);
        param.put("Imei",jsonObject.getString("Imei"));
        String time = String.valueOf(System.currentTimeMillis());
        param.put("Time",time);
        param.put("CommandCode","0039");
        param.put("ReqId",time);
        String postResult = HttpRequest.post(url)
                .header(Header.AUTHORIZATION, accessToken)//头信息，多个头信息多次调用此方法即可
                .body(param.toJSONString())//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();
        log.info("拉取设备实时位置信息：{}",postResult);
        Map map = new HashMap<>();
        map.put("data",postResult);
        return map;
    }

    @GetMapping("/device/pull/getAccessToken")
    public String getAccessToken(){
        String url = "https://openapi.miwitracker.com/api/token/get_token";
        Integer appid = 443;
        String appKey = "7BEFBF0B-ECAD-46FC-A9CA-23FAE856EEEA";
        Long timestamp = System.currentTimeMillis();
        String password = SecureUtil.md5(appKey+appid+timestamp);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Password",password);
        jsonObject.put("AppId",appid);
        jsonObject.put("Timestamp",timestamp);
        String result = HttpUtil.post(url, jsonObject.toJSONString());
        JSONObject parseObject = JSONObject.parseObject(result);
        log.info("第三方数据返回：{}",parseObject.toJSONString());
        int code = parseObject.getIntValue("Code");
        if(0 == code){
            JSONObject data = parseObject.getJSONObject("Result");
            log.info("获取第三方AccessToken：{}",data.toJSONString());
            return data.getString("AccessToken");
        }else{
            return "获取第三方AccessToken失败";
        }
    }
}
