package org.stategen.framework.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

public class CollectionUtilTst {

    @SuppressWarnings("unchecked")
    @Test
    public void tstJavaCollection() {
        User user1 = new User(1L, "张三", "班级1",1L);
        User user2 = new User(2L, "李四", "班级1",3L);
        User user3 = new User(3L, "王五", "班级2",3L);
        List<User> users = Arrays.asList(user1, user2, user3);
        
        


//        List<Long> userIds = CollectionUtil.toList(users, User::getUserId);
//        System.out.println("userIds<===========>:" + userIds);
//
//        Set<String> classSet = CollectionUtil.toSet(users, User::getClassId);
//        System.out.println("classSet<===========>:" + classSet);
//
//        Map<String, List<User>> classGroup = CollectionUtil.toGroup(users, User::getClassId);
//        System.out.println("classGroup<===========>:" + classGroup);
//
//        Map<Long, User> userIdMap = CollectionUtil.toMap(users, User::getUserId);
//        System.out.println("userIdMap<===========>:" + userIdMap);
//
//        Map<String, User> classIdMap = CollectionUtil.toMap(users, User::getClassId);
//        System.out.println("classIdMap<===========>:" + classIdMap);
//        
//        Set<Long> setForFilter =new HashSet<Long>(Arrays.asList(1L,3L));
//        List<User> userList = CollectionUtil.filterBySet(users, User::getUserId, setForFilter);
//        System.out.println("userList<===========>:" + userList);
        
        Teacher teacher1 =new Teacher(1L, "李六");
        Teacher teacher2 =new Teacher(3L, "李七");
        
        List<Teacher> teachers =Arrays.asList(teacher1,teacher2);
        Map<Long, Teacher> teacherMap = CollectionUtil.toMap(teachers,Teacher::getTeacherId);
        
//        Function<? super Teacher, String> getRealName =Teacher::getRealName;
//        Function<? super User, Long> getUserId=User::getUserId;
//        BiConsumer<User, String> setUserName =User::setUserName;
//        eachFindAndSet(users, getUserId, teacherMap, getRealName ,setUserName);
//        eachFindAndSet(users, User::getUserId, teacherMap, Teacher::getRealName ,User::setUserName);
        
        users.forEach((User user)->{
                Teacher teacher = teacherMap.get(user.getUserId());
                user.setClassId(OptionalUtil.getOrNull(teacher, Teacher::getRealName));
            }
        );
        
        CollectionUtil.setModelByMap(users, teacherMap,User::getTeachId,User::setTeacher); 
        System.out.println("users<===========>:" + users);
    }
    
    
//    @Test
//    public void testFunction(){
//        User user1 = new User(1L, "张三", "班级1");
//        Teacher teacher1 =new Teacher(1L, "李六");
//        //FunctionUtil.getAndSet(teacher1, Teacher::getRealName, user1, User::setUserName);
//        System.out.println("user1<===========>:" + user1);
//        
//    }
    
//    private void eachFindAndSet(List<User> users, Function<? super User, Object> getUserId, Map<Long, Teacher> teacherMap,
//                                Function<? super Teacher, Object> getRealName, BiConsumer<User, String> setUserName) {
//    }

    public static <K,D,S,V> void eachFindAndSet(Collection<D> dests,Function<? super D, K> destMapper,Map<K, S> sourceValues,
                                                Function<? super S, V> sourceMapper,BiConsumer<D, V> biConsumer) {
          if (CollectionUtil.isNotEmpty(dests)){
                  dests.stream().forEach(new Consumer<D>() {
                      @Override
                      public void accept(D d) {
                          K key =destMapper.apply(d);
                          S source = sourceValues.get(key);
                          if (source!=null){
                              V sourceValue = sourceMapper.apply(source);
                              biConsumer.accept(d, sourceValue);
                          }
                      }
                  });
          }
      }
    
    static class Teacher{
        Long teacherId ;
        
        String realName;
        
        public Teacher(Long teacherId, String realName){
            this.teacherId=teacherId;
            this.realName=realName;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        @Override
        public String toString() {
            return "{teacherId=" + teacherId + ", realName=" + realName + "}";
        }
    }

    static class User {
        Long   userId;
        String userName;
        String classId;
        Long teachId;
        Teacher teacher;

        public User(Long userId, String userName, String classId) {
            this.userId = userId;
            this.userName = userName;
            this.classId = classId;
        }
        public User(Long userId, String userName, String classId,Long teachId) {
            this.userId = userId;
            this.userName = userName;
            this.classId = classId;
            this.teachId =teachId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public Long getTeachId() {
            return teachId;
        }

        public void setTeachId(Long teachId) {
            this.teachId = teachId;
        }

        public Teacher getTeacher() {
            return teacher;
        }

        public void setTeacher(Teacher teacher) {
            this.teacher = teacher;
        }

        @Override
        public String toString() {
            return "{userId=" + userId + ", userName=" + userName + ", classId=" + classId + ",teacher["+teacher +"]}";
        }
    }
}
