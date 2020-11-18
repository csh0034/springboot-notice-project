package com.ask.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.ask.core.exception.InvalidationException;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoreUtils {
    protected static final Logger logger = LoggerFactory.getLogger(CoreUtils.class);

    public static class password {
        private static SecureRandom random = new SecureRandom();

        public static boolean isValid(String password, int digit) {
            // 숫자,영문,특수문자를 모두 포함하면서
            String pattern = "^(?=.*\\d)(?=.*[=\\[\\]\\{\\}/>.<,?+_~`!@#$%^&*()-])(?=.*[a-zA-Z]).{" + digit + ",999}$";
            return Pattern.matches(pattern, password);
        }

        public static String encode(String password) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.encode(password);
        }

        public static boolean compare(String rawPassword, String encodedPassword) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }

        public static String getRandomPassword() {
            String pwd = new BigInteger(130, random).toString(32);
            if (pwd.length() > 10) {
                pwd = pwd.substring(0, 10);
            }
            return pwd;
        }
    }

    public static class string extends StringUtils {

        public static String toDownloadFilename(String fileName) {
            String fname;
            try {
                fname = URLEncoder.encode(fileName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return fileName;
            }
            String[] regexs = new String[] {
                    "\\%29", "\\%28", "\\%3D", "\\%2B", "\\%7B",
                    "\\%7D", "\\%27", "\\%26", "\\%21", "\\%40",
                    "\\%23", "%24", "\\%25", "\\%5E", "\\%26",
                    "\\%2C", "\\%5B", "\\%5D", "\\+", "\\%2F" };
            String[] replchars = new String[] {
                    ")", "(", "=", "+", "{",
                    "}", "'", "&", "!", "@",
                    "#", "\\$", "%", "^", "&",
                    ",", "[", "]", " ", ","};

            for( int i = 0; i < replchars.length; i++ ) {
                fname = fname.replaceAll(regexs[i], replchars[i]);
            }
            return fname;
        }

        public static String getNewId(String prefix) {
            return prefix + UUID.randomUUID().toString().replaceAll("-", "");
        }

        public static Date toDate(String text) {
            if (StringUtils.isBlank(text)) {
                return null;
            }
            try {
                text = string.getNumberOnly(text);
                if (text.length() == 4) {
                    return date.parseDate(text, "yyyy");
                }
                else if (text.length() == 6) {
                    return date.parseDate(text, "yyyyMM");
                }
                else if (text.length() == 8) {
                    return date.parseDate(text, "yyyyMMdd");
                }
                else if (text.length() == 10) {
                    return date.parseDate(text, "yyyyMMddHH");
                }
                else if (text.length() == 12) {
                    return date.parseDate(text, "yyyyMMddHHmm");
                }
                else if (text.length() == 14) {
                    return date.parseDate(text, "yyyyMMddHHmmss");
                }
                else {
            		try {
            			return new Date(Long.parseLong(text));
        			} catch (NumberFormatException e) {
        				return null;
        			}
                }
            } catch (ParseException e) {
                return null;
            }
        }

        public static Boolean toBoolean(String text) {
            if (StringUtils.isBlank(text)) {
                return null;
            }
            text = text.trim();
            if (text.equalsIgnoreCase("true")) {
                return true;
            }
            if (text.equalsIgnoreCase("Y")) {
                return true;
            }
            if (text.equals("1")) {
                return true;
            }
            if (text.equals("1.0")) {
                return true;
            }
            if (text.equalsIgnoreCase("yes")) {
                return true;
            }
            if (text.equalsIgnoreCase("O")) {
                return true;
            }
            if (text.equalsIgnoreCase("on")) {
                return true;
            }
            return false;
        }

        public static String getNumberOnly(String str) {
            if (string.isEmpty(str)) {
                return str;
            }
            return RegExUtils.removePattern(str, "[^0-9]");
        }

        public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        public static String removeWhitespace(String str) {
            if (isEmpty(str)) {
                return str;
            }
            StringBuilder builder = new StringBuilder();
            int size = str.length();
            for (int i = 0; i < size; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    builder.append(str.charAt(i));
                }
            }
            return builder.toString();
        }

        public static boolean containsIgnoreCase(String[] arr, String value) {
            for (String item: arr) {
                if (StringUtils.equalsIgnoreCase(item, value)) {
                    return true;
                }
            }
            return false;
        }

        public static String escapedHtml(String content) {
            if (StringUtils.isEmpty(content)) {
                return content;
            }
            String s = StringEscapeUtils.escapeHtml4(content);
            s = s.replaceAll("\r\n|\n", "<br/>");
            s = s.replaceAll(" ", "&nbsp;");
            return s.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        }

        public static String escapedHtmlForInput(String content) {
            if (StringUtils.isEmpty(content)) {
                return content;
            }
            return StringEscapeUtils.escapeHtml4(content);
        }

        public static int compareIgnoreCase(String s1, String s2) {
        	s1 = (s1 == null) ? "" : s1;
        	s2 = (s2 == null) ? "" : s2;
        	return s1.compareToIgnoreCase(s2);
        }

        public static String formatFileSize(long fileSize) {
			String unit = "Bytes";
			double size = fileSize;
			if (Math.floor(size / 1024 / 1024 / 1024) > 0) {
				size = size / 1024 / 1024 / 1024;
				unit = "GB";
			}
			else if (Math.floor(size / 1024 / 1024) > 0) {
				size = size / 1024 / 1024;
				unit = "MB";
			}
			else if (Math.floor(size / 1024) > 0) {
				size = size / 1024;
				unit = "KB";
			}
			return String.format("%,.1f %s", size, unit);
        }
    }

    public static class exception {
        public static String getStackTraceString(Throwable e) {
            StringWriter  sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            try {
                sw.close();
            } catch (IOException e1) {
                logger.error("error:" + e1.getMessage());
            }
            return sw.toString();
        }
    }

    public static class filename extends FilenameUtils {

    }

    public static class date extends DateUtils {

        public static String format(Date date, String pattern) {
            if (date == null) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, LocaleContextHolder.getLocale());
            return sdf.format(date);
        }

    	public static int getDiffDays(Date beginDt, Date endDt) {
    		beginDt = date.truncate(beginDt, Calendar.DATE);
    		endDt = date.truncate(endDt, Calendar.DATE);
    		long diffTime = endDt.getTime() - beginDt.getTime();
    		int diffDays = (int)(diffTime / (1000 * 60 * 60 * 24));
    		return diffDays;
    	}

    	public static int getDiffMonths(Date beginDt, Date endDt) {
    		Calendar cal1 = Calendar.getInstance();
    		Calendar cal2 = Calendar.getInstance();

    		cal1.setTime(beginDt);
    		cal2.setTime(endDt);

    		int dy = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
    		return dy * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	}

    	public static int compareDay(Date beginDt, Date endDt) {
    		return truncatedCompareTo(beginDt, endDt, Calendar.DATE);
    	}

        public static boolean isValidDate(String date, String format) {

            if (date == null) {
                return false;
            }

            SimpleDateFormat _format = new SimpleDateFormat(format);
            boolean tmp = _format.isLenient();

            // temp for recover lenient status
            _format.setLenient(false);

            try {
                _format.parse(date);
            } catch (ParseException e) {
                return false;
            } finally {
                // recover original lenient status
                _format.setLenient(tmp);
            }
            return true;
        }

		public static String getCurrentDate(String pattern) {
			Date now = new Date();
			return format(now, pattern);
		}

    }

    public static class webutils extends WebUtils {

        public static String getRemoteIp(HttpServletRequest request) {
            String remoteIp = request.getHeader("X-Forwarded-For");
            if (string.isBlank(remoteIp)|| "unknown".equalsIgnoreCase(remoteIp)) {
                remoteIp = request.getHeader("Proxy-Client-IP");
            }
            if (string.isBlank(remoteIp) || "unknown".equalsIgnoreCase(remoteIp)) {
                remoteIp = request.getHeader("WL-Proxy-Client-IP");
            }
            if (string.isBlank(remoteIp) || "unknown".equalsIgnoreCase(remoteIp)) {
                remoteIp = request.getHeader("HTTP_CLIENT_IP");
            }
            if (string.isBlank(remoteIp) || "unknown".equalsIgnoreCase(remoteIp)) {
                remoteIp = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (string.isBlank(remoteIp) || "unknown".equalsIgnoreCase(remoteIp)) {
                remoteIp = request.getRemoteAddr();
            }

            if (string.contains(remoteIp, ",")) {
            	String[] ips = remoteIp.split(",");
            	remoteIp = ips[0].trim();
            }

            return remoteIp;
        }

        public static String getRealPath(ServletContext servletContext, String path) {
            Assert.notNull(servletContext, "ServletContext must not be null");

            if (!(path.startsWith("/"))) {
                path = "/" + path;
            }
            String realPath = servletContext.getRealPath(path);
            return realPath;
        }

        public static void downloadFile(HttpServletResponse response, File file, String filename) {
            if (file == null) {
                throw new InvalidationException("파일을 입력하십시오.");
            }
            if (!file.exists()) {
                throw new InvalidationException("파일이 없습니다.");
            }

            try (FileInputStream fi = new FileInputStream(file)) {
                byte[] buf = new byte[1024*10];
                int len = 0;
                filename = string.toDownloadFilename(filename);
                response.setContentType("application/octet-stream");
                response.setContentLength((int)file.length());
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");

                OutputStream fo = response.getOutputStream();
                while ( (len = fi.read(buf)) > 0) {
                    fo.write(buf, 0, len);
                }
                response.flushBuffer();
            } catch (FileNotFoundException e) {
                logger.error("File not found:" + file.getName());
                throw new InvalidationException("파일이 없습니다.");
            } catch (IOException e) {
                logger.error("IOException:" + e.getMessage());
                throw new InvalidationException("파일에 문제가 있어 다운로드할 수 없습니다.");
            }
        }

        public static void downloadFile(HttpServletResponse response, byte[] barr, String filename) {
            if (barr == null) {
                throw new InvalidationException("버퍼를 입력하십시오.");
            }
            if (barr.length == 0) {
                throw new InvalidationException("내용이 없습니다.");
            }

            try {
                ByteArrayInputStream is = new ByteArrayInputStream(barr);
                byte[] buf = new byte[1024*10];
                int len = 0;
                filename = string.toDownloadFilename(filename);
                response.setContentType("application/octet-stream");
                response.setContentLength(barr.length);
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");


                OutputStream fo = response.getOutputStream();
                while ( (len = is.read(buf)) > 0 ) {
                    fo.write(buf, 0, len);
                }
                is.close();
                is = null;
                response.flushBuffer();
            } catch (IOException e) {
                logger.error("IOException:" + e.getMessage());
                throw new InvalidationException("파일에 문제가 있어 다운로드할 수 없습니다.");
            }
        }

        public static boolean isAjax(HttpServletRequest request) {
            String value = request.getHeader("X-Requested-With");
            if (string.isBlank(value)) {
                return false;
            }
            return value.equalsIgnoreCase("XMLHttpRequest");
        }

		public static void downloadFile(HttpServletResponse response, File file, String filename, String contentType) {
            if (file == null) {
                throw new InvalidationException("파일을 입력하십시오.");
            }
            if (!file.exists()) {
                throw new InvalidationException("파일이 없습니다.");
            }

            FileInputStream fi = null;
            try {
                fi = new FileInputStream(file);
                byte[] buf = new byte[1024*10];
                int len = 0;
                filename = string.toDownloadFilename(filename);
                response.setContentType(contentType);
                response.setContentLength((int)file.length());
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");

                OutputStream fo = response.getOutputStream();
                while ( (len = fi.read(buf)) > 0) {
                    fo.write(buf, 0, len);
                }
                fi.close();
                fi = null;
                response.flushBuffer();
            } catch (FileNotFoundException e) {
                logger.error("File not found:" + file.getName());
                throw new InvalidationException("파일이 없습니다.");
            } catch (IOException e) {
                logger.error("IOException:" + e.getMessage());
                throw new InvalidationException("파일에 문제가 있어 다운로드할 수 없습니다.");
            } finally {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e) {
                        logger.error("error:" + e.getMessage());
                    }
                }
            }
		}
    }

    public static class file extends FileUtils {
        public static byte[] getContent(File file) {
            byte[] content = new byte[(int)file.length()];
            FileInputStream fi = null;
            try {
                fi = new FileInputStream(file);
                byte[] buf = new byte[1024*10];
                int pos = 0;
                int len = 0;
                while ( (len = fi.read(buf)) > 0) {
                    System.arraycopy(buf, 0, content, pos, len);
                    pos += len;
                }
            } catch (IOException e) {
                logger.error(exception.getStackTraceString(e));
            } finally {
                try {
                    if (fi != null) {
                        fi.close();
                    }
                } catch (IOException e) {
                    logger.error(exception.getStackTraceString(e));
                }
            }
            fi = null;
            return content;
        }
    }

    public static class property extends PropertyUtils {
        public static void copyProperties(Object dest, Object orig) {
            try {
                PropertyUtils.copyProperties(dest, orig);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error(exception.getStackTraceString(e));
                throw new RuntimeException(e);
            }
        }
    }

    public static class io extends IOUtils {

    }

    public static class json {
        private static final ObjectMapper mapper = new ObjectMapper();

        public static String toString(Object obj) {
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        public static <T> T toObject(String json, Class<T> cls) {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            try {
                return mapper.readValue(json, cls);
            } catch (IOException e) {
                logger.error(exception.getStackTraceString(e));
                return null;
            }
        }

        @SuppressWarnings("unchecked")
		public static Map<String, Object> toMap(Object obj) {
        	return mapper.convertValue(obj, Map.class);
        }

        public static Map<String, String> toStringMap(Object obj) {
        	return mapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
        }
    }

    public static class number extends NumberUtils {

    }

    public static class session {

        public static HttpSession getSession(boolean force) {
            ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder
                    .getRequestAttributes();
            HttpSession session = attr.getRequest().getSession(force);
            return session;
        }

        public static String getSessionId() {
            return getSession(true).getId();
        }

        public static void invalidate() {
            HttpSession session = getSession(false);
            if (session == null) {
                return;
            }

            Enumeration<String> eNum = session.getAttributeNames();
            String attributeName = null;
            while (eNum.hasMoreElements()) {
                attributeName = eNum.nextElement();
                session.removeAttribute(attributeName);
            }
            session.invalidate();
            SecurityContext sc = SecurityContextHolder.getContext();
            if (sc != null) {
                sc.setAuthentication(null);
            }
        }
    }
}
