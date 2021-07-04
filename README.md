## 简介
zoro-time 是一个简单的日期和时间操作工具

`Zoro` 是一个带有时区的包含日期和时间的工具类，包含
1. 获取当前（指定）时间的年、月、日、星期、时、分、秒
2. 在指定的日期时间字段值（年、月、日、星期、时、分、秒等）上进行加减
3. 任意更改指定的日期时间字段的值
4. 获取两个时间的差值（相差几年或相差几个月或相差几天等）
5. 格式化为文本或解析日期时间文本，同时可创建可缓存的 `DateTimeFormatter`，避免重复创建相同模式的 `DateTimeFormatter`

需要指明的是，与 Java8 的日期时间 API 不同，`Zoro` 是可变对象，在其上的修改，不会创建一个新的对象

可以使用 `clone` 或 `create(Zoro)` 方法拷贝一个新的 `Zoro` 实例

## API 示例

### 创建 `Zoro`
```java
// 本地时区的当前时间
Zoro.now();
// 本地时区的前一天的时间
Zoro.yesterday();
```

按照指定的模式解析时间文本
```java
Zoro.parse(text, pattern);
```

使用时间戳创建。[, ZoneId] 表示ZoneId是选填的，默认为本地时区
```java
Zoro.create(timestamp [, ZoneId]);
```

使用 Java 日期 API 创建
```java
Zoro.create(Date [, ZoneId]);
Zoro.create(Calendar);
Zoro.create(ZonedDateTime);
Zoro.create(LocalDateTime);
Zoro.create(LocalDate);
...
```

### 获取一些基本信息
```java
zoro.year();// 获取年份
zoro.month();// 获取月份
zoro.weekOfMonth();// 获取当前日期是当月的第几周
zoro.weekOfYear();// 获取当前日期是当年的第几周
zoro.dayOfWeek();// 获取当前日期是周几
zoro.dayOfMonth();// 当前日期在当月是第几天
zoro.dayOfYear();// 当前日期在当年是第几天
zoro.hour();// 小时
...
zoro.millisecond();// 当前时间的毫秒值
zoro.nano();// 当前时间的纳秒值
zoro.zodiacsSign();// 当前日期的所处星座
zoro.monthDays();// 当前日期的月份总共的天数
zoro.yearDays();// 当前年份总共的天数
zoro.isLeapYear();// 是否是闰年
zoro.timestamp();// UTC 时间戳

zoro.toDate();// 转换为 java.util.Date
zoro.toCalendar();// 转换为 Calendar
zoro.toZonedDateTime();// 转换为 ZonedDateTime
zoro.toLocalDateTime();// 转换为 LocalDateTime
zoro.toLocalDate();
zoro.toLocalTime();
```

### 在`Zoro`的基础上进行修改
```java
// 在当前基础上增加或减少指定值
// 负值表示减少，正值表示增加，下同
zoro.addYears(years);
zoro.addMonths(months);
zoro.addWeeks(weeks);
zoro.addDays(days);
...
zoro.addNanos(nanos);

// 变更时区
zoro.withZoneId(ZoneId);

// 将当前日期的指定字段更改为指定值
zoro.withYear(year);
zoro.withMonth(month);
...
zoro.withLastDayOfMonth();// 当月的最后一天
zoro.withFirstInMonth(DayOfWeek);// 当月的第一个周几
zoro.with(TemporalAdjuster);
```

### 两个日期的比较
```java
start.diffOfYears(end);// 两个日期相差的年数
start.diffOfMonths(end);// 两个日期相差的月数
...
start.diffOfWeeks();// 两个日期相差的周数
```

## 安装
### Maven
```xml
<dependency>
    <groupId>com.github.jonzhang3</groupId>
    <artifactId>zoro-time</artifactId>
    <version>1.0.0</version>
</dependency>
```
