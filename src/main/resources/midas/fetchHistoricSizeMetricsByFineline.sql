select
    finelineNbr,
    sizeDesc,
    lyActualSalesUnits,
    lyActualSalesUnitsPct,
    lyActualReceiptUnits,
    lyActualReceiptUnitsPct
from
    (
        select
            0 as rowNbr,
            oics.fineline_nbr as finelineNbr,
            'ALL' as sizeDesc,
            sum(oics.lyActualSalesUnits) as lyActualSalesUnits,
            100.0 as lyActualSalesUnitsPct,
            sum(oics.lyActualReceiptUnits) as lyActualReceiptUnits,
            100.0 as lyActualReceiptUnitsPct
        from
            (
                select
                    item_nbr,
                    catlg_item_id,
                    omni_seg_nbr,
                    omni_dept_nbr,
                    omni_catg_grp_nbr,
                    omni_catg_nbr,
                    omni_subcatg_nbr
                from
                    aex_true_demand.omni_hierarchy_subcatg_v2_local
                where
                        omni_subcatg_nbr = :lvl4Nbr
                  and omni_catg_nbr = :lvl3Nbr
                  and (omni_catg_grp_nbr = :lvl2Nbr
                    or :lvl2Nbr is null)
                  and (omni_dept_nbr = :lvl1Nbr
                    or :lvl1Nbr is null)
                  and (omni_seg_nbr = :lvl0Nbr
                    or :lvl0Nbr is null)) ohs
                inner join (
                select
                    oics.fineline_nbr,
                    oics.item_nbr,
                    oics.catlg_item_id,
                    'ALL' as sizeDesc,
                    sum(oics.actual_sales_unit) as lyActualSalesUnits,
                    100.0 as lyActualSalesUnitsPct,
                    sum(actual_receipt_units) as lyActualReceiptUnits,
                    100.0 as lyActualReceiptUnitsPct
                from
                    aex_true_demand.omni_item_channel_sales_wkly_v4_local oics,
                    (
                        select
                            distinct sml.modified_size,
                                     sml.actual_size
                        from
                            aex_true_demand.size_mapping_local sml
                        where
                                sml.dept_nbr = :lvl1Nbr
                          and sml.dept_catg_nbr = :lvl3Nbr) sml
                where
                        oics.fineline_nbr = :finelineNbr
                  and oics.size = sml.actual_size
                  and oics.wm_yr_wk_nbr between :lyCompWeekStart and :lyCompWeekEnd
                  and (oics.current_channel in (:channel, 'omni')
                    or :channel is null)
                  and ((oics.actual_sales_unit is not null
                    and oics.actual_sales_unit > 0)
                    or (oics.actual_receipt_units is not null
                        and oics.actual_receipt_units > 0))
                group by
                    fineline_nbr,
                    oics.item_nbr,
                    oics.catlg_item_id ) oics on
                        ohs.item_nbr = oics.item_nbr
                    and ohs.catlg_item_id = oics.catlg_item_id
        group by
            finelineNbr,
            sizeDesc
        union all
        select
            (rowNumberInAllBlocks() + 1) as rowNbr,
            finelineNbr,
            sizeDesc,
            lySalesUnits as lyActualSalesUnits,
            round(divide(lySalesUnits * 100.0,
                         nullif(lyTotalSalesUnits,
                                0)),
                  2) as lyActualSalesUnitsPct,
            lyReceiptUnits as lyActualReceiptUnits,
            round(divide(lyReceiptUnits * 100.0,
                         nullif(lyTotalReceiptUnits,
                                0)),
                  2) as lyActualReceiptUnitsPct
        from
            (
                select
                    oics.fineline_nbr as finelineNbr,
                    oics.size as sizeDesc,
                    sum(oics.actual_sales_unit) as lySalesUnits,
                    sum(lySalesUnits) over () as lyTotalSalesUnits,
                        sum(oics.actual_receipt_units) as lyReceiptUnits,
                    sum(lyReceiptUnits) over () as lyTotalReceiptUnits
                from
                    (
                        select
                            item_nbr,
                            catlg_item_id,
                            omni_seg_nbr,
                            omni_dept_nbr,
                            omni_catg_grp_nbr,
                            omni_catg_nbr,
                            omni_subcatg_nbr
                        from
                            aex_true_demand.omni_hierarchy_subcatg_v2_local
                        where
                                omni_subcatg_nbr = :lvl4Nbr
                          and omni_catg_nbr = :lvl3Nbr
                          and (omni_catg_grp_nbr = :lvl2Nbr
                            or :lvl2Nbr is null)
                          and (omni_dept_nbr = :lvl1Nbr
                            or :lvl1Nbr is null)
                          and (omni_seg_nbr = :lvl0Nbr
                            or :lvl0Nbr is null) ) ohs
                        inner join (
                        select
                            oics.item_nbr,
                            oics.catlg_item_id,
                            oics.fineline_nbr,
                            sml.modified_size as size,
				sum(oics.actual_sales_unit) as actual_sales_unit,
				sum(oics.actual_receipt_units) as actual_receipt_units
                        from
                            aex_true_demand.omni_item_channel_sales_wkly_v4_local oics,
                            (
                            select
                            distinct sml.modified_size,
                            sml.actual_size
                            from
                            aex_true_demand.size_mapping_local sml
                            where
                            sml.dept_nbr = :lvl1Nbr
                            and sml.dept_catg_nbr = :lvl3Nbr) sml
                        where
                            oics.fineline_nbr = :finelineNbr
                          and oics.size = sml.actual_size
                          and oics.wm_yr_wk_nbr between :lyCompWeekStart and :lyCompWeekEnd
                          and (oics.current_channel in (:channel, 'omni')
                           or :channel is null)
                          and ((oics.actual_sales_unit is not null
                          and oics.actual_sales_unit > 0)
                           or (oics.actual_receipt_units is not null
                          and oics.actual_receipt_units > 0))
                        group by
                            oics.item_nbr,
                            oics.catlg_item_id,
                            oics.fineline_nbr,
                            sml.modified_size ) oics on
                                ohs.item_nbr = oics.item_nbr
                            and ohs.catlg_item_id = oics.catlg_item_id
                group by
                    oics.fineline_nbr,
                    oics.size
                order by
                    lySalesUnits desc ) sales)
order by
    rowNbr