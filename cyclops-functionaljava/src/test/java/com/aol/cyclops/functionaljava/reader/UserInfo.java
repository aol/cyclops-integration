package com.aol.cyclops.functionaljava.reader;

import java.util.HashMap;
import java.util.Map;

import com.aol.cyclops.functionaljava.FJ;

import fj.data.Reader;

public class UserInfo implements Users {

	public Reader<UserRepository,Map<String,String> > userInfo(String username) {
		
		FJ.ForReader.each2(findUser(username), user ->getUser(user.getSupervisor().getId())
		 		,(user,boss) -> "user:"+username+" boss is "+boss.getName());
		
		
		return FJ.ForReader.each2(findUser(username),
				 user -> getUser(user.getSupervisor().getId())
				 ,(user,boss) -> buildMap(user,boss));
		
		
	}

	private Map<String,String>  buildMap(User user, User boss) {
		return new HashMap<String,String> (){{
				put("fullname",user.getName());
				put("email",user.getEmail());
				put("boss",boss.getName());
				
		}};
	}
}
