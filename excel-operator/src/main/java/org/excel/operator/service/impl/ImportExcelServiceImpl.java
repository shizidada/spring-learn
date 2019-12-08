package org.excel.operator.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.excel.operator.entity.ExcelInfoDO;
import org.excel.operator.es.repository.ImportExcelRepository;
import org.excel.operator.mapper.ExcelInfoMapper;
import org.excel.operator.service.ImportExcelService;
import org.excel.operator.service.model.ImportExcelModel;
import org.excel.operator.util.PageInfoUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/10/27 15:10
 * @see org.excel.operator.service.impl
 */
@Service
public class ImportExcelServiceImpl implements ImportExcelService {

  @Resource
  private ExcelInfoMapper excelInfoMapper;

  @Resource
  private ImportExcelRepository importExcelRepository;

  @Override public Map<String, Object> selectAll(ImportExcelModel importExcelModel) {
    if (importExcelModel.getPageNum() > 0 && importExcelModel.getPageSize() > 0) {
      PageHelper.startPage(importExcelModel.getPageNum(), importExcelModel.getPageSize());
    }
    ExcelInfoDO excelInfoDO = this.convertImportExcelModel2ImportExcelDO(importExcelModel);
    List<ExcelInfoDO> list = excelInfoMapper.selectAll(excelInfoDO);
    PageInfo page = new PageInfo<>(list);
    Map<String, Object> basePageInfo = PageInfoUtils.getBasePageInfo(page);
    return basePageInfo;
  }

  @Override public ImportExcelModel selectByPrimaryKey(Long id) {
    ExcelInfoDO excelInfoDO = excelInfoMapper.selectByPrimaryKey(id);
    ImportExcelModel importExcelModel = this.convertModelFromDataObject(excelInfoDO);
    return importExcelModel;
  }

  @Override public ImportExcelModel selectByImportExcel(ImportExcelModel importExcelModel) {
    ExcelInfoDO excelInfoDO = this.convertImportExcelModel2ImportExcelDO(importExcelModel);
    excelInfoDO = excelInfoMapper.selectByImportExcel(excelInfoDO);
    ImportExcelModel excelModel = this.convertModelFromDataObject(excelInfoDO);
    return excelModel;
  }

  @Override public int addImportExcelRecord(ImportExcelModel importExcelModel) {
    ExcelInfoDO excelInfoDO = this.convertImportExcelModel2ImportExcelDO(importExcelModel);
    return excelInfoMapper.addImportExcelRecord(excelInfoDO);
  }

  @Override public int addBatchImportExcelRecord(List<ImportExcelModel> importExcelModels) {
    List<ExcelInfoDO> excelInfoDOList = importExcelModels.stream().map(importExcelModel -> {
      ExcelInfoDO excelInfoDO = this.convertImportExcelModel2ImportExcelDO(importExcelModel);
      return excelInfoDO;
    }).collect(Collectors.toList());
    
    int result = excelInfoMapper.addImportExcelRecordBatch(excelInfoDOList);

    // es 操作
    // Iterable<ImportExcelDoc> excelDocs =
    importExcelRepository.saveAll(excelInfoDOList);
    return result;
  }

  @Override public List<ImportExcelModel> exportSameReceiverAndPhoneAndAddress() {
    List<ExcelInfoDO> excelInfoDOList =
        excelInfoMapper.selectSameReceiverAndPhoneAndAddress();

    List<ImportExcelModel> importExcelModels = excelInfoDOList.stream().map(excelInfoDO -> {
      ImportExcelModel itemModel = this.convertModelFromDataObject(excelInfoDO);
      return itemModel;
    }).collect(Collectors.toList());

    return importExcelModels;
  }

  @Override public List<ImportExcelModel> exportDiffReceiverAndPhoneAndAddress() {
    List<ExcelInfoDO> excelInfoDOList =
        excelInfoMapper.selectDiffReceiverAndPhoneAndAddress();
    List<ImportExcelModel> importExcelModels = excelInfoDOList.stream().map(excelInfoDO -> {
      ImportExcelModel importExcelModel = this.convertModelFromDataObject(excelInfoDO);
      return importExcelModel;
    }).collect(Collectors.toList());
    return importExcelModels;
  }
 

  /**
   * 将 ImportExcelModel 转为 ExcelInfoDO
   *
   * @return ExcelInfoDO
   */
  private ExcelInfoDO convertImportExcelModel2ImportExcelDO(ImportExcelModel importExcelModel) {
    if (importExcelModel == null) {
      return null;
    }
    ExcelInfoDO excelInfoDO = new ExcelInfoDO();
    BeanUtils.copyProperties(importExcelModel, excelInfoDO);
    return excelInfoDO;
  }

  /**
   * 将 ExcelInfoDO 转为 ImportExcelModel
   *
   * @return ImportExcelModel
   */
  private ImportExcelModel convertModelFromDataObject(ExcelInfoDO excelInfoDO) {
    ImportExcelModel importExcelModel = new ImportExcelModel();
    BeanUtils.copyProperties(excelInfoDO, importExcelModel);
    return importExcelModel;
  }
}
