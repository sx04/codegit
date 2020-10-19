package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author sunjunjie
 * @date 2020/8/24 13:43
 */
@Entity
public class TaskTempInfo {

        private int id;

        private String name;

        private String webName;

        private String sectionTitle;

        private String groupId;

        private String sectionUrl;

        private int isRun;

        private int isCorrect;

        @Id
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWebName() {
            return webName;
        }

        public void setWebName(String webName) {
            this.webName = webName;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public void setSectionTitle(String sectionTitle) {
            this.sectionTitle = sectionTitle;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getSectionUrl() {
            return sectionUrl;
        }

        public void setSectionUrl(String sectionUrl) {
            this.sectionUrl = sectionUrl;
        }

        public int getIsRun() {
            return isRun;
        }

        public void setIsRun(int isRun) {
            this.isRun = isRun;
        }

        public int getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(int isCorrect) {
            this.isCorrect = isCorrect;
        }
}
