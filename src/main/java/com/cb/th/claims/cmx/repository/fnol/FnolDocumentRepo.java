package com.cb.th.claims.cmx.repository.fnol;


import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import com.cb.th.claims.cmx.entity.fnol.FnolDocument;
import com.cb.th.claims.cmx.enums.fnol.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FnolDocumentRepo extends JpaRepository<FnolDetail, Long> {

}
