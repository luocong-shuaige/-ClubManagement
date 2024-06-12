package com.bishe.club.controller;

import com.bishe.club.entity.Members;
import com.bishe.club.entity.Users;
import com.bishe.club.utils.CacheHandle;
import com.bishe.club.utils.PageData;
import com.bishe.club.utils.R;
import com.bishe.club.service.UsersService;
import com.bishe.club.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bishe.club.service.MembersService;

/**
 * 系统请求响应控制器
 * 成员信息
 */
@Controller
@RequestMapping("/members")
public class
MembersController extends BaseController {

    protected static final Logger Log = LoggerFactory.getLogger(MembersController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private MembersService membersService;

    @RequestMapping("")
    public String index() {
        return "pages/Members";
    }

    @GetMapping("/info")
    @ResponseBody
    public R getInfo(String id) {

        Log.info("查找指定成员信息，ID：{}", id);
        Members members = membersService.getOne(id);
        return R.successData(members);
    }

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize,
                          String token, String teamName, String userName) {

        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user.getType() == 0) {
            Log.info("分页查找成员信息，当前页码：{}，"
                            + "每页数据量：{}, 模糊查询，团队名称：{}，用户姓名：{}", pageIndex,
                    pageSize, teamName, userName);
            PageData page = membersService.getPageAll(pageIndex, pageSize, teamName, userName);
            return R.successData(page);
        } else {

            Log.info("分页查找成员信息，当前页码：{}，"
                            + "每页数据量：{}, 模糊查询，团队名称：{}，用户姓名：{}", pageIndex,
                    pageSize, teamName, userName);
            PageData page = membersService.getPageByManId(pageIndex, pageSize, user.getId(), teamName, userName);
            return R.successData(page);
        }
    }


    @PostMapping("/add")
    @ResponseBody
    public R addInfo(Members members) {

        members.setId(IDUtils.makeIDByCurrent());
        Log.info("添加成员信息，传入参数：{}", members);
        membersService.add(members);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(Members members) {

        Log.info("修改成员信息，传入参数：{}", members);
        membersService.update(members);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String id) {

        Members members = membersService.getOne(id);
        if(membersService.isManager(members.getTeamId(), members.getUserId())){
            return R.warn("社团管理员无法移除");
        }else{
            Log.info("删除成员信息, ID:{}", id);
            membersService.delete(members);
            return R.success();
        }
    }
}