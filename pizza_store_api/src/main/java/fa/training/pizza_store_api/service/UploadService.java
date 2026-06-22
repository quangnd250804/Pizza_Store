package fa.training.pizza_store_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fa.training.pizza_store_api.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class UploadService {
    @Autowired
    private Cloudinary cloudinary;

    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(400, "File không được để trống");
        }

        try {
            // Cấu hình đẩy file vào folder cố định và tự động nhận diện định dạng file
            Map<?, ?> options = ObjectUtils.asMap(
                    "folder", "pizza_store",
                    "resource_type", "auto");

            // Tiến hành upload lên Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            return uploadResult.get("secure_url").toString();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AppException(500, "Quá trình tải ảnh lên Cloudinary gặp sự cố kỹ thuật!");
        }
    }
}
