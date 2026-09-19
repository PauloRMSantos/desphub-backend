package desphub.pds.backend.services;

import desphub.pds.backend.dtos.offices.CreateOfficeDTO;
import desphub.pds.backend.dtos.offices.OfficeResponseDTO;
import desphub.pds.backend.mappers.OfficeMapper;
import desphub.pds.backend.models.Office;
import desphub.pds.backend.repositories.IOfficeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OfficeService {

    private final IOfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    public OfficeService(IOfficeRepository officeRepository, OfficeMapper officeMapper) {
        this.officeRepository = officeRepository;
        this.officeMapper = officeMapper;
    }

    @Transactional
    public OfficeResponseDTO create(CreateOfficeDTO dto) {
        if (officeRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um escritório com esse CPF/CNPJ");
        }
        Office office = officeMapper.toEntity(dto);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Transactional(readOnly = true)
    public List<OfficeResponseDTO> findAll() {
        return officeRepository.findAll().stream()
                .map(officeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OfficeResponseDTO findById(Long id) {
        return officeMapper.toResponse(findEntityOr404(id));
    }

    @Transactional
    public void delete(Long id) {
        if (!officeRepository.existsById(id)) {
            throw notFound(id);
        }
        officeRepository.deleteById(id);
    }

    private Office findEntityOr404(Long id) {
        return officeRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Escritório não encontrado: " + id);
    }
}
