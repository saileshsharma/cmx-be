

/*
This is a multi-line comment.
You can span multiple lines.


SELECT indexname
FROM pg_indexes
WHERE tablename = 'fnol';

*/


ALTER INDEX uk_sl695t33cnhna2ehmbbaueaa4 RENAME TO fnol_index_addr_loc_id;
ALTER INDEX uk_o987034x58l3w5avmj0cotw88 RENAME TO fnoldetail_index_fnol_unique;
ALTER INDEX uk_qsobmwr798ygnjx3y61pcbdt2 RENAME TO insured_index_addressid;
ALTER INDEX uk_n6hn87ykbes9xquhmoj3fhn7l RENAME TO policy_index_vehicleid;

