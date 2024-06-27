尚庭公寓是一个公寓租赁平台项目，包含**移动端**和**后台管理系统**，其中移动端面向广大用户，提供找房、看房预约、租约管理等功能，后台管理系统面向管理员，提供公寓（房源）管理、租赁管理、用户管理等功能

### 6.1.2 部署MySQL

在`server01`部署MySQL，具体步骤可[参考文档](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/linux-installation-yum-repo.html)。

1. **安装MySQL yum库**

   - **下载yum库**

     下载地址为https://dev.mysql.com/downloads/repo/yum/。需要根据操作系统选择相应版本，Centos7需选择`mysql80-community-release-el7-9.noarch.rpm`。

     执行以下命令可直接下载到服务器

     ```bash
     wget https://dev.mysql.com/get/mysql80-community-release-el7-9.noarch.rpm
     ```

    - **安装yum库**

      在上述`rpm`文件所在路径执行如下命令

      ```bash
      rpm -ivh mysql80-community-release-el7-9.noarch.rpm
      ```

    - **配置国内镜像**

      修改`/etc/yum.repo.d/mysql-community.repo`文件中的`[mysql80-community]`中的`baseUrl`参数，修改内容如下：

      ```ini
      [mysql80-community]
      name=MySQL 8.0 Community Server
      baseurl=https://mirrors.tuna.tsinghua.edu.cn/mysql/yum/mysql-8.0-community-el7-$basearch/
      enabled=1
      gpgcheck=1
      gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql-2022
             file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql
      ```


2. **安装MySQL**

   执行如下命令安装MySQL

   ```bash
   yum install -y mysql-community-server
   ```

3. **启动MySQL**

   执行如下命令启动MySQL服务

     ```bash
   systemctl start mysqld
     ```

     执行以下命令查看MySQL运行状态

     ```bash
   systemctl status mysqld
     ```


4. **root用户相关配置**

   - **查看root用户初始密码**

     MySQL启动后会将root用户的初始密码写入日志，通过以下命令可以获取密码

     ```bash
     cat /var/log/mysqld.log | grep password
     ```

    - **使用初始密码登录**

      执行以下命令登录MySQL

      ```bash
      mysql -uroot -p'password'
      ```

   - **修改root用户密码**

     ```bash
     ALTER USER 'root'@'localhost' IDENTIFIED BY 'Atguigu.123';
     ```

     **注意**：MySQL默认安装了[validate_password](https://dev.mysql.com/doc/refman/8.0/en/validate-password.html) 插件，默认情况下，要求密码要包含大写字母、小写字母、数字和特殊符号，且密码长度最小为8。若需设置简单密码，可禁用该插件，或调整该插件的密码强度级别。

   - **授予root用户远程登录权限**

     ```bash
     CREATE USER 'root'@'%' IDENTIFIED BY 'Atguigu.123';
     GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
     FLUSH PRIVILEGES;
     ```

### 6.1.3 部署Redis

在`server01`部署Redis服务，安装方式采用yum在线安装，安装版本为`redis-7.0.13`，具体步骤如下

1. **安装Redis yum仓库**

   - **下载yum仓库**

     Redis所在的仓库为**remi-release**，下载地址为：http://rpms.famillecollet.com/enterprise/remi-release-7.rpm，可使用如下命令直接下载到服务器

     ```bash
     wget http://rpms.famillecollet.com/enterprise/remi-release-7.rpm
     ```

   - **安装yum仓库**

     执行如下命令进行安装

     ```bash
     rpm -ivh remi-release-7.rpm
     ```

2. **安装Reids**

   执行以下命令安装Redis

   ```bash
   yum --enablerepo=remi -y install redis-7.0.13
   ```

   注：`--enablerepo`选项的作用为启用一个仓库

3. **配置Redis允许远程访问**

   Redis服务默认只允许本地访问，若需要进行远程访问，需要做出以下配置。

   修改Redis配置文件

   ```bash
   vim /etc/redis/redis.conf
   ```

   修改如下参数

   ```ini
   #监听所有网络接口，默认只监听localhost
   bind 0.0.0.0
   
   #关闭保护模式，默认开启。开始保护模式后，远程访问必须进行认证后才能访问。
   protected-mode no
   ```

4. **启动Redis**

   执行以下命令启动Redis

   ```bash
   systemctl start redis
   ```

   执行以下命令查看Redis的运行状态

   ```bash
   systemctl status redis
   ```

   执行以下命令设置Redis开机自启

   ```bash
   systemctl enable redis
   ```

### 6.1.4 部署MinIO

在`server01`部署MinIO，安装方式采用rpm离线安装，具体步骤可参考[官方文档](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-single-node-single-drive.html#minio-snsd)。

1. **获取MinIO安装包**

   下载地址如下：https://dl.min.io/server/minio/release/linux-amd64/archive/minio-20230809233022.0.0.x86_64.rpm，通过以下命令可直接将安装包下载至服务器

   ```bash
   wget https://dl.min.io/server/minio/release/linux-amd64/archive/minio-20230809233022.0.0.x86_64.rpm
   ```

   注：若下载缓慢，大家可直接使用课程资料中附带的安装包

2. **安装MinIO**

   ```bash
   rpm -ivh minio-20230809233022.0.0.x86_64.rpm
   ```

3. **集成Systemd**

   - **Systemd概述**

     `Systemd`是一个广泛应用于Linux系统的系统初始化和服务管理器，其可以管理系统中的各种服务和进程，包括启动、停止和重启服务，除此之外，其还可以监测各服务的运行状态，并在服务异常退出时，自动拉起服务，以保证服务的稳定性。系统自带的防火墙服务`firewalld`，我们自己安装的`mysqld`和`redis`均是由`Systemd`进行管理的，此处将MinIO服务也交给Systemd管理。

   - **编写MinIO服务配置文件**

     Systemd所管理的服务需要由一个配置文件进行描述，这些配置文件均位于`/etc/systemd/system/`或者`/usr/lib/systemd/system/`目录下，下面创建MinIO服务的配置文件。

     执行以下命令创建并打开`minio.service`文件

     ```bash
     vim /etc/systemd/system/minio.service
     ```

     内容如下，具体可参考MinIO[官方文档](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-single-node-single-drive.html#create-the-systemd-service-file)。

     ```ini
     [Unit]
     Description=MinIO
     Documentation=https://min.io/docs/minio/linux/index.html
     Wants=network-online.target
     After=network-online.target
     AssertFileIsExecutable=/usr/local/bin/minio
     
     [Service]
     WorkingDirectory=/usr/local
     ProtectProc=invisible
     EnvironmentFile=-/etc/default/minio
     ExecStartPre=/bin/bash -c "if [ -z \"${MINIO_VOLUMES}\" ]; then echo \"Variable MINIO_VOLUMES not set in /etc/default/minio\"; exit 1; fi"
     ExecStart=/usr/local/bin/minio server $MINIO_OPTS $MINIO_VOLUMES
     Restart=always
     LimitNOFILE=65536
     TasksMax=infinity
     TimeoutStopSec=infinity
     SendSIGKILL=no
     
     [Install]
     WantedBy=multi-user.target
     ```

     **注意**：

     重点关注上述文件中的以下内容即可

     - `EnvironmentFile`，该文件中可配置MinIO服务所需的各项参数
     - `ExecStart`，该参数用于配置MinIO服务的启动命令，其中`$MINIO_OPTS`、`$MINIO_VOLUMES`，均引用于`EnvironmentFile`中的变量。
       - `MINIO_OPTS`用于配置MinIO服务的启动选项，可省略不配置。
       - `MINIO_VOLUMES`用于配置MinIO服务的数据存储路径。
     - `Restart`，表示自动重启

   - **编写`EnvironmentFile`文件**

     执行以下命令创建并打开`/etc/default/minio`文件

     ```bash
     vim /etc/default/minio
     ```

     内容如下，具体可参考[官方文档](https://min.io/docs/minio/linux/operations/install-deploy-manage/deploy-minio-single-node-single-drive.html#create-the-environment-variable-file)。

     ```ini
     MINIO_ROOT_USER=minioadmin
     MINIO_ROOT_PASSWORD=minioadmin
     MINIO_VOLUMES=/data
     MINIO_OPTS="--console-address :9001"
     ```

     **注意**

     - `MINIO_ROOT_USER`和`MINIO_ROOT_PASSWORD`为用于访问MinIO的用户名和密码，**密码长度至少8位**。

     - `MINIO_VOLUMES`用于指定数据存储路径，需确保指定的路径是存在的，可执行以下命令创建该路径。

       ```bash
       mkdir /data
       ```

     - `MINIO_OPTS`中的`console-address`,用于指定管理页面的地址。

4. **启动MinIO**

   执行以下命令启动MinIO

   ```bash
   systemctl start minio
   ```

   执行以下命令查询运行状态

   ```bash
   systemctl status minio
   ```

   设置MinIO开机自启

   ```bash
   systemctl enable minio
   ```

5. **访问MinIO管理页面**

   管理页面的访问地址为：`http://192.168.10.101:9001`

   **注意**：

   `ip`需要根据实际情况做出修改
