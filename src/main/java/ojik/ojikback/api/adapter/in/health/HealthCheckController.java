package ojik.ojikback.api.adapter.in.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ojik.ojikback.api.adapter.out.common.ApiResponse;
import ojik.ojikback.api.adapter.out.health.dto.HealthCheckResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "서버 상태 확인 API")
public class HealthCheckController {

    @Operation(summary = "헬스 체크", description = "서버 상태를 확인한다.")
    @SecurityRequirements
    @GetMapping("/api/health")
    public ApiResponse<HealthCheckResponseData> health() {
        return ApiResponse.success(new HealthCheckResponseData("UP"));
    }
}
