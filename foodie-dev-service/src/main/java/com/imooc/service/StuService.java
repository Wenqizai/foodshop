package com.imooc.service;

import com.imooc.pojo.Stu;

/**
 * @author Wenqi Liang
 * @date 2020/12/1
 */
public interface StuService {

    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);

    public void saveParent();

    public void saveChildren();
}
