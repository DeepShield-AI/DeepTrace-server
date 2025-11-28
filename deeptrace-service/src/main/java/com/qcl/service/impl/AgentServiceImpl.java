package com.qcl.service.impl;

import com.qcl.service.AgentService;
import com.qcl.utils.Constants;
import com.qcl.utils.OkHttpUtil;
import com.qcl.vo.Result;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {


    public ResponseEntity<String> forwardRequest(String param) {
        String url = Constants.AGENT_ADDRESS + "/api/native-agent";

        try {
            // 使用我们的工具类发送请求
            Response response = OkHttpUtil.postJsonWithResponse(
                    url,
                    param,
                    null  // 可以在这里添加请求头
            );

            if (response.isSuccessful()) {
                return ResponseEntity.ok(Constants.SUCCESS);
            }else{
                return ResponseEntity.status(response.code()).body(response.message());
            }
        } catch (IOException e) {
            //e.printStackTrace()
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public Result<String> forwardGet(String param) {
        String url = Constants.AGENT_ADDRESS + "/api/native-agent";
        url="https://www.cnblogs.com/wuyongyin/p/16468864.html";

        try {
            // 使用我们的工具类发送请求
            Response response = OkHttpUtil.getWithResponse(
                    url,
                    null
            );

            if (response.isSuccessful()) {
                return Result.success();
            }else{
                return Result.error(response.code(), response.message());
            }
        } catch (IOException e) {
            //e.printStackTrace()
            log.error(e.getMessage());
            return Result.error("Error: " + e.getMessage());
        }
    }



}
