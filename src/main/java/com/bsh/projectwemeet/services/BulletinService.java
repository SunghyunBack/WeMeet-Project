package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.BulletinEntity;
import com.bsh.projectwemeet.mappers.BulletinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class BulletinService {

    private final BulletinMapper bulletinMapper;

    @Autowired
    public BulletinService(BulletinMapper bulletinMapper){
        this.bulletinMapper=bulletinMapper;
    }

    public boolean putComment(HttpServletRequest request, BulletinEntity bulletinEntity){
        bulletinEntity.setDeleted(false)
                .setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"));

        return this.bulletinMapper.insertComment(bulletinEntity) > 0;
    }

}
