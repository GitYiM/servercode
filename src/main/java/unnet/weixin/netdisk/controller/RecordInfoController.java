package unnet.weixin.netdisk.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import unnet.weixin.netdisk.entity.RestResult;
import unnet.weixin.netdisk.entity.UploadInfo;
import unnet.weixin.netdisk.services.UploadInfoService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author cy
 * @Description
 * @Date 2020/10/20
 */

@CrossOrigin(maxAge = 3600, allowCredentials = "true")
@RestController
@ResponseBody
@RequestMapping("/record")
public class RecordInfoController {

    @Resource
    private UploadInfoService uploadInfoService;

    /**
     * 根据id删除上传记录
     * @param ids
     * @param userId
     * @return
     */
    @ApiOperation("根据id删除上传记录")
    @ResponseBody
    @PostMapping("/deleteRecord")
    public RestResult<?> deleteById(@RequestParam("ids") List<Long> ids, @RequestParam("userId") String userId) {
        int i = uploadInfoService.deleteById(ids, userId);
        if (i == 0){
            return new RestResult<>(-1, "上传记录删除失败");
        }
        return new RestResult<>(0, "上传记录删除成功");
    }

    @ApiOperation("根据userid查询上传记录")
    @ResponseBody
    @GetMapping("/getRecord")
    public RestResult<?> selectByUserId(@RequestParam("userId") String userId) {
        List<UploadInfo> uploadInfos = uploadInfoService.selectByUserId(userId);
        return new RestResult<>(0, "上传记录查询成功", uploadInfos);
    }
}
