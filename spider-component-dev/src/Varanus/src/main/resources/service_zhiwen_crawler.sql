-- --------------------------------------------------------
-- 主机:                           192.168.1.252
-- 服务器版本:                        5.7.22 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出  表 service_zhiwen_crawler.doc_detail 结构
DROP TABLE IF EXISTS `doc_detail`;
CREATE TABLE IF NOT EXISTS `doc_detail` (
  `doc_id` int(32) NOT NULL AUTO_INCREMENT COMMENT '文章唯一标识符',
  `list_id` int(32) NOT NULL COMMENT '列表唯一标识符',
  `task_id` int(32) NOT NULL COMMENT '任务唯一标识符',
  `doc_client_type` varchar(256) NOT NULL COMMENT '文章请求工具，webclient或webdrive',
  `doc_request_type` varchar(256) NOT NULL COMMENT '文章http请求类型，get或post',
  `doc_request_params` varchar(256) DEFAULT NULL COMMENT '文章请求参数，存储为json字符',
  `doc_response_type` varchar(256) NOT NULL COMMENT '文章http返回类型，html或xml或yml或json',
  `doc_json_field` varchar(256) DEFAULT NULL COMMENT '如果文章返回类型为json，文章url所在字段的key值',
  `doc_need_proxy` tinyint(1) unsigned zerofill NOT NULL COMMENT '文章请求是否需要代理，0-无效 1-有效',
  `title_xpath` varchar(256) NOT NULL COMMENT '文章标题xpath',
  `info_box_xpath` varchar(256) DEFAULT NULL COMMENT '信息盒区域网页源码',
  `content_xpath` varchar(256) NOT NULL COMMENT '文章正文xpath',
  `policy_date_xpath` varchar(256) NOT NULL COMMENT '政策日期xpath',
  `publish_office_xpath` varchar(256) DEFAULT NULL COMMENT '发文机关xpath',
  `site_width` int(32) NOT NULL COMMENT '网站宽度',
  `publish_date_xpath` varchar(256) DEFAULT NULL COMMENT '发布日期xpath',
  `attachment_xpath` varchar(256) DEFAULT NULL COMMENT '附件xpath',
  `attachment_basepath` varchar(256) DEFAULT NULL COMMENT '附件路径',
  `image_xpath` varchar(256) DEFAULT NULL COMMENT '图片xpath',
  `image_basepath` varchar(256) DEFAULT NULL COMMENT '图片路径',
  `draft_date_xpath` varchar(256) DEFAULT NULL COMMENT '成文日期xpath',
  `index_number_xpath` varchar(256) DEFAULT NULL COMMENT '索引号xpath',
  `topic_type_xpath` varchar(256) DEFAULT NULL COMMENT '主题分类xpath',
  `reference_number_xpath` varchar(256) DEFAULT NULL COMMENT '发文字号xpath',
  `topic_words_xpath` varchar(256) DEFAULT NULL COMMENT '主题词xpath',
  `summary_xpath` varchar(256) DEFAULT NULL COMMENT '内容概述xpath',
  `form_code_xpath` varchar(256) DEFAULT NULL COMMENT '形式代码xpath',
  `theme_xpath` varchar(256) DEFAULT NULL COMMENT '体裁xpath',
  `effective_date_xpath` varchar(256) DEFAULT NULL COMMENT '生效日期xpath',
  `expire_date_xpath` varchar(256) DEFAULT NULL COMMENT '失效日期xpath',
  `source_xpath` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`doc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='文章详情表';

-- 数据导出被取消选择。
-- 导出  表 service_zhiwen_crawler.list_detail 结构
DROP TABLE IF EXISTS `list_detail`;
CREATE TABLE IF NOT EXISTS `list_detail` (
  `list_id` int(32) NOT NULL AUTO_INCREMENT COMMENT '列表唯一标识符',
  `task_id` int(32) NOT NULL COMMENT '任务唯一标识符',
  `list_template_url` varchar(8192) DEFAULT NULL COMMENT '第二页及以后列表规则模板url',
  `list_page_number` int(32) NOT NULL COMMENT '文章列表总页数',
  `list_xpath` varchar(256) NOT NULL COMMENT '列表网页的xpath',
  `list_client_type` varchar(256) NOT NULL COMMENT '列表请求工具，webclient或webdrive',
  `list_request_type` varchar(256) NOT NULL COMMENT '列表http请求类型，get或post',
  `list_request_params` varchar(8192) DEFAULT NULL COMMENT '列表请求参数，存储为json字符串',
  `list_need_proxy` tinyint(1) unsigned zerofill NOT NULL COMMENT '列表请求是否需要代理，0-无效 1-有效',
  `list_response_type` varchar(256) NOT NULL COMMENT '列表http返回类型，html或xml或yml或json',
  `list_json_field` varchar(256) DEFAULT NULL COMMENT '如果列表返回类型为json，文章url所在字段的key值',
  `list_repeat` tinyint(1) unsigned zerofill DEFAULT NULL COMMENT '是否重复抓取列表数据，0-否 1-是',
  `list_page_name` varchar(255) DEFAULT NULL COMMENT 'post请求的page翻页参数名',
  `last_crawler_url` varchar(255) DEFAULT NULL COMMENT '最后一篇爬取的文章',
  `list_json_key` varchar(255) DEFAULT NULL COMMENT 'json的key的关系，用,隔开，最后一个key为field数组',
  `list_json_substring` varchar(255) DEFAULT NULL COMMENT '返回是含json的字符时，需截取json，开始字符和截止字符用,隔开',
  `list_id_url` varchar(255) DEFAULT NULL COMMENT '返回字段为url的id时，需要自己拼文章url',
  `json_id_key` varchar(255) DEFAULT NULL COMMENT '返回json为id时的字段名',
  PRIMARY KEY (`list_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='列表详情表';

-- 数据导出被取消选择。
-- 导出  表 service_zhiwen_crawler.official_document 结构
DROP TABLE IF EXISTS `official_document`;
CREATE TABLE IF NOT EXISTS `official_document` (
  `key_id` varchar(256) NOT NULL COMMENT '公文唯一标识符UUID',
  `doc_id` int(32) DEFAULT NULL COMMENT 'doc_detail表主键',
  `list_id` int(32) DEFAULT NULL COMMENT 'list_detail表主键',
  `task_id` int(32) DEFAULT NULL COMMENT 'task_basic_info表主键',
  `url` varchar(8192) DEFAULT NULL COMMENT '公文网页url',
  `source` varchar(256) DEFAULT NULL COMMENT '文章来源',
  `department` varchar(256) DEFAULT NULL COMMENT '公文发布部门机构名称',
  `title` varchar(256) DEFAULT NULL COMMENT '公文标题',
  `content` mediumtext COMMENT '公文正文',
  `text_html` mediumtext NOT NULL COMMENT '正文源码',
  `section_title` varchar(256) DEFAULT NULL COMMENT '章节标题',
  `topic` varchar(256) DEFAULT NULL COMMENT '主题，导航栏主题信息',
  `index_number_info` varchar(256) DEFAULT NULL COMMENT '索引号',
  `topic_cat_info` varchar(256) DEFAULT NULL COMMENT '信息盒的主题分类信息',
  `pub_office_info` varchar(256) DEFAULT NULL COMMENT '信息盒发文机关信息',
  `draft_date_info` varchar(256) DEFAULT NULL COMMENT '信息盒成文日期',
  `text_title_info` varchar(256) DEFAULT NULL COMMENT '信息盒标题信息',
  `reference_number_info` varchar(256) DEFAULT NULL COMMENT ' 信息盒的发文字号',
  `pub_date_info` varchar(256) DEFAULT NULL COMMENT '信息盒的发布日期',
  `topic_words_info` varchar(256) DEFAULT NULL COMMENT '信息盒主题词',
  `department_info` varchar(256) DEFAULT NULL COMMENT '信息盒信息的发布部门',
  `summary_info` varchar(256) DEFAULT NULL COMMENT '信息盒的内容概述',
  `form_code_info` varchar(256) DEFAULT NULL COMMENT '信息盒的形式代码',
  `theme_info` varchar(256) DEFAULT NULL COMMENT '信息盒的体裁',
  `effective_date_info` varchar(256) DEFAULT NULL COMMENT '信息盒的生效日期',
  `expire_date_info` varchar(256) DEFAULT NULL COMMENT '信息盒的失效日期',
  `source_html` varchar(256) DEFAULT NULL COMMENT '网站源代码',
  `insert_date` varchar(256) DEFAULT NULL COMMENT '文档插入时间',
  `update_date` varchar(256) DEFAULT NULL COMMENT '文档更新时间',
  `area` varchar(256) DEFAULT NULL COMMENT '网站所在行政区域',
  `is_normal` tinyint(1) NOT NULL DEFAULT '1' COMMENT '网站是否正规',
  `policy_date` varchar(256) DEFAULT NULL COMMENT '政策日期',
  `site_width` varchar(256) DEFAULT NULL COMMENT '网站宽度',
  `imgs` text COMMENT '图片存储信息（存储路径等）',
  `attachments` text COMMENT '附件存储信息（url，存储路径等）',
  `repeat_count` int(32) DEFAULT NULL COMMENT '用于判断重复入Redis次数',
  `is_clean` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '数据是否被清洗，0为未清洗，1为已经清洗 ',
  PRIMARY KEY (`key_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公文信息表';

-- 数据导出被取消选择。
-- 导出  表 service_zhiwen_crawler.task_basic_info 结构
DROP TABLE IF EXISTS `task_basic_info`;
CREATE TABLE IF NOT EXISTS `task_basic_info` (
  `task_id` int(32) NOT NULL AUTO_INCREMENT COMMENT '任务唯一标识符',
  `sql_table` varchar(256) NOT NULL COMMENT 'mysql存储表名',
  `department` varchar(256) NOT NULL COMMENT '文章发布部门',
  `area` varchar(256) NOT NULL COMMENT '文章发布部门所属行政区域',
  `section_title` varchar(256) NOT NULL COMMENT '任务板块标题',
  `section_url` varchar(8192) NOT NULL COMMENT '板块url',
  `is_valid` tinyint(1) unsigned zerofill NOT NULL COMMENT '配置是否有效，0-无效 1-有效',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文章插入时间',
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '文章更新时间',
  `url_availability` tinyint(1) unsigned zerofill DEFAULT '1' COMMENT '板块列表首页url网络是否可达，0-无效 1-有效',
  `site_version` varchar(16) DEFAULT NULL COMMENT '网站版本号',
  `version_exception_count` int(11) DEFAULT NULL COMMENT '网站版本异常次数',
  `responsible_people` varchar(255) NOT NULL COMMENT '任务添加负责人',
  PRIMARY KEY (`task_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='任务基本信息表';

-- 数据导出被取消选择。
-- 导出  表 service_zhiwen_crawler.task_warning 结构
DROP TABLE IF EXISTS `task_warning`;
CREATE TABLE IF NOT EXISTS `task_warning` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '报警自增id',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `list_warning_type` int(255) DEFAULT NULL COMMENT '列表配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败 ',
  `list_warning_detail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '列表配置报警描述',
  `doc_warning_type` int(255) DEFAULT NULL COMMENT '文章详情配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败',
  `doc_warning_detail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章详情配置报警描述',
  `warning_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '报警时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2296 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
