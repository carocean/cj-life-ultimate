package cj.life.ability.minio.service;

import cj.life.ability.api.exception.ApiException;
import cj.life.ability.minio.INetDiskService;
import cj.life.ability.minio.util.FilePath;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetDiskService implements INetDiskService {
    @Resource
    MinioClient minioClient;

    /**
     * 创建目录，如果是文件则报错
     *
     * @param path
     */
    @Override
    @SneakyThrows
    public void mkdir(String path) {
        FilePath filePath = FilePath.parse(path);
        if (filePath.isFile()) {
            throw new ApiException("4005", "The path is path of file.");
        }
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(filePath.getBucketName()).build())) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(filePath.getBucketName())
                            .build()
            );
        }
        for (String objectName : filePath.listRelativePath()) {
            putDirObject(filePath.getBucketName(), objectName);
        }
    }

    @Override
    @SneakyThrows
    public List<String> listChildren(FilePath filePath, boolean recursive) {
        String objectName = filePath.getRelativePath();
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        List<String> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(filePath.getBucketName()).prefix(objectName).recursive(recursive).build());
        if (objectsIterator != null) {
            for (Result<Item> o : objectsIterator) {
                Item item = o.get();
                String name = item.objectName();
                int pos = name.indexOf(objectName);
                if (pos < 0) {
                    continue;
                }
                name = name.substring(objectName.length());
                if (!StringUtils.hasText(name)) {
                    continue;
                }
                list.add(name);
            }
        }
        return list;
    }

    @Override
    @SneakyThrows
    public List<String> listChildren(String path, boolean recursive) {
        FilePath filePath = FilePath.parse(path);
        return listChildren(filePath, recursive);
    }

    @Override
    @SneakyThrows
    public InputStream readFile(FilePath filePath) {
        if (!filePath.isFile()) {
            throw new ApiException("4005", "The path is not a file");
        }
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return minioClient
                .getObject(GetObjectArgs.builder().bucket(filePath.getBucketName()).object(objectName).build());
    }

    @Override
    @SneakyThrows
    public InputStream readFile(String path) {
        FilePath filePath = FilePath.parse(path);
        return readFile(filePath);
    }

    @Override
    @SneakyThrows
    public InputStream readFile(FilePath filePath, long offset, long length) {
        if (!filePath.isFile()) {
            throw new ApiException("4005", "The path is not a file");
        }
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return minioClient
                .getObject(GetObjectArgs.builder().bucket(filePath.getBucketName()).object(objectName).offset(offset).length(length).build());
    }

    @Override
    @SneakyThrows
    public InputStream readFile(String path, long offset, long length) {
        FilePath filePath = FilePath.parse(path);
        return readFile(filePath, offset, length);
    }

    @Override
    @SneakyThrows
    public void writeFile(MultipartFile file, String path) {
        FilePath filePath = FilePath.parse(path);
        if (!FilePath.isDir(path)) {
            throw new ApiException("4005", "The path is not a directory.");
        }
        if (!existsDir(filePath.getBucketName(), filePath.getRelativePath())) {
            throw new ApiException("4004", "The path does not exists.");
        }
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        objectName = String.format("%s%s", objectName, file.getOriginalFilename());
        if (existsFile(filePath.getBucketName(), objectName)) {
            throw new ApiException("4005", "The file already exist.");
        }
        InputStream inputStream = file.getInputStream(); //文件流
        minioClient.putObject(PutObjectArgs.builder() //使用minio客户端put方法
                .bucket(filePath.getBucketName()) //桶名称
                .object(objectName) //文件名称
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        inputStream.close();
    }

    @Override
    @SneakyThrows
    public void writeFile(File file, String path) {
        FilePath filePath = FilePath.parse(path);
        if (!FilePath.isDir(path)) {
            throw new ApiException("4005", "The path is not a directory.");
        }
        if (!existsDir(filePath.getBucketName(), filePath.getRelativePath())) {
            throw new ApiException("4004", "The path does not exists.");
        }
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }

        objectName = String.format("%s%s", objectName, file.getName());
        if (existsFile(filePath.getBucketName(), objectName)) {
            throw new ApiException("4005", "The file already exist.");
        }
        InputStream inputStream = new FileInputStream(file); //文件流
        minioClient.putObject(PutObjectArgs.builder() //使用minio客户端put方法
                .bucket(filePath.getBucketName()) //桶名称
                .object(objectName) //文件名称
                .stream(inputStream, file.length(), -1)
//                .contentType(inputStream.getContentType())
                .build());
        inputStream.close();
    }

    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件夹名称
     * @return true：存在
     */
    @SneakyThrows
    private boolean existsDir(String bucketName, String objectName) {
        if ("/".equals(objectName)) {//也就是桶，视为桶存在则目录存在
            return true;
        }
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.objectName().endsWith("/") && item.objectName().startsWith(objectName)) {
                    exist = true;
                    break;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 对象
     * @return true：存在
     */
    @SneakyThrows
    private boolean existsFile(String bucketName, String objectName) {
        if ("/".equals(objectName)) {//也就是桶，视为桶存在则目录存在
            return true;
        }
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        boolean exist = true;
        try {
            minioClient
                    .statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw e;
        }
        return true;
    }

    /**
     * 创建文件夹或目录
     *
     * @param bucketName 存储桶
     * @param objectName 目录路径
     */
    @SneakyThrows
    private ObjectWriteResponse putDirObject(String bucketName, String objectName) {
        if (!objectName.endsWith("/")) {
            objectName = objectName + "/";
        }
        return minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    @Override
    @SneakyThrows
    public StatObjectResponse getFileInfo(FilePath filePath) {
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder().bucket(filePath.getBucketName()).object(objectName).build());
        return response;
    }

    @Override
    @SneakyThrows
    public void empty(String path) {
        FilePath filePath = FilePath.parse(path);
        if (!filePath.isDir()) {
            throw new ApiException("4005", "the path is not a directory.");
        }
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        if ("".equals(objectName)) {
            List<String> list = listChildren(filePath, true);
            for (String oname : list) {
                oname = String.format("%s%s", objectName, oname);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(filePath.getBucketName()).object(oname).build());
            }
            return;
        }
        if (filePath.isDir()) {
            List<String> list = listChildren(filePath, true);
            for (String oname : list) {
                oname = String.format("%s%s", objectName, oname);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(filePath.getBucketName()).object(oname).build());
            }
        }
    }

    @Override
    @SneakyThrows
    public void delete(String path) {
        FilePath filePath = FilePath.parse(path);
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        if ("".equals(objectName)) {
            List<String> list = listChildren(filePath, true);
            for (String oname : list) {
                oname = String.format("%s%s", objectName, oname);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(filePath.getBucketName()).object(oname).build());
            }
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(filePath.getBucketName()).build());
            return;
        }
        if (filePath.isDir()) {
            List<String> list = listChildren(filePath, true);
            for (String oname : list) {
                oname = String.format("%s%s", objectName, oname);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(filePath.getBucketName()).object(oname).build());
            }
        }
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(filePath.getBucketName()).object(objectName).build());
    }

    @Override
    @SneakyThrows
    public boolean exists(String path) {
        FilePath filePath = FilePath.parse(path);
        if ("/".equals(filePath.getRelativePath())) {
            return existsBucket(filePath.getBucketName());
        }
        if (FilePath.isFile(path)) {
            return existsFile(filePath.getBucketName(), filePath.getRelativePath());
        }
        return existsDir(filePath.getBucketName(), filePath.getRelativePath());
    }

    @SneakyThrows
    private boolean existsBucket(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @Override
    @SneakyThrows
    public String accessUrl(String path, int expirySeconds) {
        FilePath filePath = FilePath.parse(path);
        String objectName = filePath.getRelativePath();
        while (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        if ("".equals(objectName)) {
            throw new ApiException("4005", "The path is empty.");
        }
        if (expirySeconds < 0) {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(filePath.getBucketName())
                    .object(objectName)
                    .build());
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                .builder()
                .method(Method.GET)
                .bucket(filePath.getBucketName())
                .object(objectName).expiry(expirySeconds, TimeUnit.SECONDS) //预览可用时间
                .build());
    }
}
