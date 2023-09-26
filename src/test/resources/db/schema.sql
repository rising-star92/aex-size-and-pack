-- DROP SCHEMA dbo;

--CREATE SCHEMA dbo;
-- us_wm_aex_spo.dbo.channel_text definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.channel_text;

CREATE TABLE channel_text (
                              channel_id tinyint NOT NULL,
                              channel_desc varchar(15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                              CONSTRAINT PK__channel___2D0861AB078BDDD9 PRIMARY KEY (channel_id)
);


-- us_wm_aex_spo.dbo.fixturetype_rollup definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.fixturetype_rollup;

CREATE TABLE fixturetype_rollup (
                                    fixturetype_rollup_id smallint NOT NULL,
                                    fixturetype_rollup_name nvarchar(75) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    fixturetype_rollup_desc nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    CONSTRAINT PK__fixturet__1CAA56EB49021E1B PRIMARY KEY (fixturetype_rollup_id)
);


-- us_wm_aex_spo.dbo.fp_strategy_text definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.fp_strategy_text;

CREATE TABLE fp_strategy_text (
                                  flow_strategy_code tinyint NOT NULL,
                                  flow_strategy_desc varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  CONSTRAINT PK__fp_strat__04125C387001E104 PRIMARY KEY (flow_strategy_code)
);


-- us_wm_aex_spo.dbo.run_status_text definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.run_status_text;

CREATE TABLE run_status_text (
                                 run_status_code tinyint NOT NULL,
                                 run_status_desc varchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                 run_status_long_desc varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                 CONSTRAINT PK__run_stat__5DCC3506E1275EA8 PRIMARY KEY (run_status_code)
);


-- us_wm_aex_spo.dbo.run_status_text foreign keys


-- us_wm_aex_spo.dbo.select_status_text definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.select_status_text;

CREATE TABLE select_status_text (
                                    select_status_id tinyint NOT NULL,
                                    select_status_desc varchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    CONSTRAINT PK__select_s__202A8AC2628FF83C PRIMARY KEY (select_status_id)
);


-- us_wm_aex_spo.dbo.sp_cc_chan_size definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sp_cc_chan_size;

CREATE TABLE sp_cc_chan_size (
                                 plan_id bigint NOT NULL,
                                 rpt_lvl_0_nbr int NOT NULL,
                                 rpt_lvl_1_nbr int NOT NULL,
                                 rpt_lvl_2_nbr int NOT NULL,
                                 rpt_lvl_3_nbr int NOT NULL,
                                 rpt_lvl_4_nbr int NOT NULL,
                                 fineline_nbr smallint NOT NULL,
                                 style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                 customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                 channel_id tinyint NOT NULL,
                                 ahs_size_id bigint NOT NULL,
                                 ahs_size_desc varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                 flow_strategy_code tinyint NULL,
                                 merch_method_code smallint NULL,
                                 merch_method_short_desc nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                 bump_pack_qty bigint NULL,
                                 initial_set_qty bigint NULL,
                                 buy_qty bigint NULL,
                                 repln_qty bigint NULL,
                                 adj_repln_qty bigint NULL,
                                 store_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                 CONSTRAINT PK__sp_cc_ch__12ED1D77D3D10E72 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id,ahs_size_id)
);


-- us_wm_aex_spo.dbo.spo_color_combination definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.spo_color_combination;

CREATE TABLE spo_color_combination (
                                       color_combo_id bigint IDENTITY(1,1) NOT NULL,
                                       plan_id bigint NOT NULL,
                                       rpt_lvl_0_nbr int NOT NULL,
                                       rpt_lvl_1_nbr int NOT NULL,
                                       rpt_lvl_2_nbr int NOT NULL,
                                       rpt_lvl_3_nbr int NOT NULL,
                                       rpt_lvl_4_nbr int NOT NULL,
                                       fineline_nbr smallint NOT NULL,
                                       color_combo_desc nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                       CONSTRAINT PK__spo_colo__E5A2F3E7B394FACA PRIMARY KEY (color_combo_id)
);


-- us_wm_aex_spo.dbo.sysdiagrams definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sysdiagrams;

CREATE TABLE sysdiagrams (
                             name sysname COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                             principal_id int NOT NULL,
                             diagram_id int IDENTITY(1,1) NOT NULL,
                             version int NULL,
                             definition varbinary(MAX) NULL,
                             CONSTRAINT PK__sysdiagr__C2B05B6148E8A74E PRIMARY KEY (diagram_id),
                             CONSTRAINT UK_principal_name UNIQUE (principal_id,name)
);


-- us_wm_aex_spo.dbo.z_fp_cc_bppk_sum definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.z_fp_cc_bppk_sum;

CREATE TABLE z_fp_cc_bppk_sum (
                                  plan_id bigint NOT NULL,
                                  rpt_lvl_0_nbr int NOT NULL,
                                  rpt_lvl_1_nbr int NOT NULL,
                                  rpt_lvl_2_nbr int NOT NULL,
                                  rpt_lvl_3_nbr int NOT NULL,
                                  rpt_lvl_4_nbr int NOT NULL,
                                  fineline_nbr smallint NOT NULL,
                                  style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  bump_pack_nbr tinyint NOT NULL,
                                  wm_yr_wk smallint NULL,
                                  fiscal_week_desc char(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  weeks_supply tinyint NULL,
                                  bump_pack_eff_yr_wk smallint NULL,
                                  bump_pack_yr_wk_desc char(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  alloc_units_pct decimal(7,2) NULL,
                                  bump_pack_qty bigint NULL,
                                  initial_set_qty bigint NULL,
                                  initial_set_qty_per_fixtr bigint NULL,
                                  CONSTRAINT PK__z_fp_cc___9FC8258D18B3C2DE PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,bump_pack_nbr)
);


-- us_wm_aex_spo.dbo.z_fp_cc_metrics_wk definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.z_fp_cc_metrics_wk;

CREATE TABLE z_fp_cc_metrics_wk (
                                    plan_id bigint NOT NULL,
                                    rpt_lvl_0_nbr int NOT NULL,
                                    rpt_lvl_1_nbr int NOT NULL,
                                    rpt_lvl_2_nbr int NOT NULL,
                                    rpt_lvl_3_nbr int NOT NULL,
                                    rpt_lvl_4_nbr int NOT NULL,
                                    fineline_nbr smallint NOT NULL,
                                    style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    wm_yr_wk smallint NOT NULL,
                                    fiscal_week_desc char(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    initial_set_qty bigint NULL,
                                    bump_pack_qty bigint NULL,
                                    sales_units bigint NULL,
                                    sales_dollars decimal(15,4) NULL,
                                    reciepts_units int NULL,
                                    receipts_dollars decimal(15,4) NULL,
                                    nbr_stores int NULL,
                                    sales_pct decimal(9,2) NULL,
                                    sell_through_pct decimal(9,2) NULL,
                                    initial_markup_pct decimal(9,2) NULL,
                                    dc_begin_inv bigint NULL,
                                    dc_end_inv bigint NULL,
                                    store_begin_inv bigint NULL,
                                    store_end_inv bigint NULL,
                                    adj_reciept_units int NULL,
                                    CONSTRAINT PK__z_fp_cc___8BAB8D924A22D10B PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,wm_yr_wk)
);


-- us_wm_aex_spo.dbo.z_strat_cc_sp_clus definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.z_strat_cc_sp_clus;

CREATE TABLE z_strat_cc_sp_clus (
                                    plan_id bigint NOT NULL,
                                    strategy_id bigint NOT NULL,
                                    analytics_cluster_id int NOT NULL,
                                    rpt_lvl_0_nbr int NOT NULL,
                                    rpt_lvl_1_nbr int NOT NULL,
                                    rpt_lvl_2_nbr int NOT NULL,
                                    rpt_lvl_3_nbr int NOT NULL,
                                    rpt_lvl_4_nbr int NOT NULL,
                                    fineline_nbr smallint NOT NULL,
                                    style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                    channel_id tinyint NOT NULL,
                                    analytics_sp_pct decimal(7,3) NULL,
                                    merchant_override_sp_pct decimal(7,3) NULL,
                                    size_profile_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    CONSTRAINT PK__z_strat___1DED962D5FA2A72A PRIMARY KEY (plan_id,strategy_id,analytics_cluster_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id)
);


-- us_wm_aex_spo.dbo.z_strat_fl_vg_clus definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.z_strat_fl_vg_clus;

CREATE TABLE z_strat_fl_vg_clus (
                                    plan_id bigint NOT NULL,
                                    strategy_id bigint NOT NULL,
                                    analytics_cluster_id int NOT NULL,
                                    rpt_lvl_0_nbr int NOT NULL,
                                    rpt_lvl_1_nbr int NOT NULL,
                                    rpt_lvl_2_nbr int NOT NULL,
                                    rpt_lvl_3_nbr int NOT NULL,
                                    rpt_lvl_4_nbr int NOT NULL,
                                    fineline_nbr smallint NOT NULL,
                                    in_instock_ind bit NULL,
                                    gross_sales_units bigint NULL,
                                    analytics_volume_dev decimal(7,3) NULL,
                                    merchant_override_volume_dev decimal(7,3) NULL,
                                    CONSTRAINT PK__z_strat___AEE6A75CA45353F3 PRIMARY KEY (plan_id,strategy_id,analytics_cluster_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr)
);


-- us_wm_aex_spo.dbo.analytics_ml_send definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.analytics_ml_send;

CREATE TABLE analytics_ml_send (
                                   analytics_send_id bigint IDENTITY(1,1) NOT NULL,
                                   plan_id bigint NOT NULL,
                                   strategy_id bigint NULL,
                                   analytics_cluster_id bigint NULL,
                                   rpt_lvl_0_nbr int NOT NULL,
                                   rpt_lvl_1_nbr int NOT NULL,
                                   rpt_lvl_2_nbr int NOT NULL,
                                   rpt_lvl_3_nbr int NOT NULL,
                                   rpt_lvl_4_nbr int NOT NULL,
                                   fineline_nbr smallint NOT NULL,
                                   first_name nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   last_name nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   run_status_code tinyint NULL,
                                   analytics_send_desc nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                   start_ts datetime2 NULL,
                                   end_ts datetime2 NULL,
                                   retry_cnt smallint NULL,
                                   payload_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   return_message nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   analytics_job_id char(36) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   CONSTRAINT PK__analytic__282ED881D9DCFC84 PRIMARY KEY (analytics_send_id),
                                   CONSTRAINT FK_ams1 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code)
);
CREATE NONCLUSTERED INDEX ix_ams1 ON dbo.analytics_ml_send (  plan_id ASC  , rpt_lvl_0_nbr ASC  , rpt_lvl_1_nbr ASC  , rpt_lvl_2_nbr ASC  , rpt_lvl_3_nbr ASC  , rpt_lvl_4_nbr ASC  , fineline_nbr ASC  )
	 WITH (  PAD_INDEX = OFF ,FILLFACTOR = 100  ,SORT_IN_TEMPDB = OFF , IGNORE_DUP_KEY = OFF , STATISTICS_NORECOMPUTE = OFF , ONLINE = OFF , ALLOW_ROW_LOCKS = ON , ALLOW_PAGE_LOCKS = ON  )
	 ON [PRIMARY ] ;


-- us_wm_aex_spo.dbo.analytics_child_send definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.analytics_child_send;

CREATE TABLE analytics_child_send (
                                      analytics_child_send_id bigint IDENTITY(1,1) NOT NULL,
                                      analytics_send_id bigint NOT NULL,
                                      run_status_code tinyint NULL,
                                      analytics_send_desc nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                      start_ts datetime2 NULL,
                                      end_ts datetime2 NULL,
                                      retry_cnt smallint NULL,
                                      payload_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      return_message nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      analytics_job_id char(36) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      bump_pack_nbr tinyint NULL,
                                      CONSTRAINT PK__analytic__690E5C7A18239166 PRIMARY KEY (analytics_child_send_id)
);


-- us_wm_aex_spo.dbo.analytics_child_send foreign keys

ALTER TABLE analytics_child_send ADD CONSTRAINT FK_zacs1 FOREIGN KEY (analytics_send_id) REFERENCES analytics_ml_send(analytics_send_id);


-- us_wm_aex_spo.dbo.merchcatg_plan definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.merchcatg_plan;

CREATE TABLE merchcatg_plan (
                                plan_id bigint NOT NULL,
                                rpt_lvl_0_nbr int NOT NULL,
                                rpt_lvl_1_nbr int NOT NULL,
                                rpt_lvl_2_nbr int NOT NULL,
                                rpt_lvl_3_nbr int NOT NULL,
                                channel_id tinyint NOT NULL,
                                rpt_lvl_0_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                rpt_lvl_1_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                rpt_lvl_2_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                rpt_lvl_3_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                CONSTRAINT PK__merchcat__531B104F55CD7D2D PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                CONSTRAINT FK_mp1 FOREIGN KEY (channel_id) REFERENCES channel_text(channel_id)
);


-- us_wm_aex_spo.dbo.merchcatg_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.merchcatg_replpk_cons;

CREATE TABLE merchcatg_replpk_cons (
                                       plan_id bigint NOT NULL,
                                       rpt_lvl_0_nbr int NOT NULL,
                                       rpt_lvl_1_nbr int NOT NULL,
                                       rpt_lvl_2_nbr int NOT NULL,
                                       rpt_lvl_3_nbr int NOT NULL,
                                       channel_id tinyint NOT NULL,
                                       final_buy_units bigint NULL,
                                       repl_units bigint NULL,
                                       vendor_pack_cnt bigint NULL,
                                       whse_pack_cnt bigint NULL,
                                       vnpk_whpk_ratio decimal(7,3) NULL,
                                       repl_pack_cnt bigint NULL,
                                       CONSTRAINT PK__merchcat__531B104FCB4DF760 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                       CONSTRAINT FK_mrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id) REFERENCES merchcatg_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                       CONSTRAINT FK_mrc2 FOREIGN KEY (channel_id) REFERENCES channel_text(channel_id)
);


-- us_wm_aex_spo.dbo.plan_admin_rules definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.plan_admin_rules;

CREATE TABLE plan_admin_rules (
                                  plan_id bigint NOT NULL,
                                  channel_id tinyint NOT NULL,
                                  repl_item_peice_rule int NULL,
                                  min_repl_item_units int NULL,
                                  CONSTRAINT PK__plan_adm__1C4F090783507EC2 PRIMARY KEY (plan_id,channel_id),
                                  CONSTRAINT FK_par1 FOREIGN KEY (channel_id) REFERENCES channel_text(channel_id)
);


-- us_wm_aex_spo.dbo.rc_merchcatg_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_merchcatg_replpk_fixtr_cons;

CREATE TABLE rc_merchcatg_replpk_fixtr_cons (
                                                plan_id bigint NOT NULL,
                                                rpt_lvl_0_nbr int NOT NULL,
                                                rpt_lvl_1_nbr int NOT NULL,
                                                rpt_lvl_2_nbr int NOT NULL,
                                                rpt_lvl_3_nbr int NOT NULL,
                                                fixturetype_rollup_id smallint NOT NULL,
                                                channel_id tinyint NOT NULL,
                                                final_buy_units bigint NULL,
                                                repl_units bigint NULL,
                                                vendor_pack_cnt bigint NULL,
                                                whse_pack_cnt bigint NULL,
                                                vnpk_whpk_ratio decimal(7,3) NULL,
                                                run_status_code tinyint NOT NULL,
                                                repl_pack_cnt bigint NULL,
                                                fixturetype_rollup_name nvarchar(75) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                                CONSTRAINT PK__rc_merch__780EB9786596D7DD PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,fixturetype_rollup_id,channel_id),
                                                CONSTRAINT FK_rmrfc1 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code)
);


-- us_wm_aex_spo.dbo.rc_subcatg_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_subcatg_replpk_fixtr_cons;

CREATE TABLE rc_subcatg_replpk_fixtr_cons (
                                              plan_id bigint NOT NULL,
                                              rpt_lvl_0_nbr int NOT NULL,
                                              rpt_lvl_1_nbr int NOT NULL,
                                              rpt_lvl_2_nbr int NOT NULL,
                                              rpt_lvl_3_nbr int NOT NULL,
                                              rpt_lvl_4_nbr int NOT NULL,
                                              fixturetype_rollup_id smallint NOT NULL,
                                              channel_id tinyint NOT NULL,
                                              final_buy_units bigint NULL,
                                              repl_units bigint NULL,
                                              vendor_pack_cnt bigint NULL,
                                              whse_pack_cnt bigint NULL,
                                              vnpk_whpk_ratio decimal(7,3) NULL,
                                              run_status_code tinyint NOT NULL,
                                              repl_pack_cnt bigint NULL,
                                              fixturetype_rollup_name nvarchar(75) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                              CONSTRAINT PK__rc_subca__E50923A2437F3475 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fixturetype_rollup_id,channel_id),
                                              CONSTRAINT FK_rsrfc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,fixturetype_rollup_id,channel_id) REFERENCES rc_merchcatg_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,fixturetype_rollup_id,channel_id),
                                              CONSTRAINT FK_rsrfc2 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code)
);


-- us_wm_aex_spo.dbo.sp_fl_chan_fixtr definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sp_fl_chan_fixtr;

CREATE TABLE sp_fl_chan_fixtr (
                                  plan_id bigint NOT NULL,
                                  rpt_lvl_0_nbr int NOT NULL,
                                  rpt_lvl_1_nbr int NOT NULL,
                                  rpt_lvl_2_nbr int NOT NULL,
                                  rpt_lvl_3_nbr int NOT NULL,
                                  rpt_lvl_4_nbr int NOT NULL,
                                  fineline_nbr smallint NOT NULL,
                                  channel_id tinyint NOT NULL,
                                  fixturetype_rollup_id smallint NOT NULL,
                                  flow_strategy_code tinyint NULL,
                                  merch_method_code smallint NULL,
                                  merch_method_short_desc smallint NULL,
                                  bump_pack_qty bigint NULL,
                                  initial_set_qty bigint NULL,
                                  buy_qty bigint NULL,
                                  repln_qty bigint NULL,
                                  adj_repln_qty bigint NULL,
                                  store_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  bump_pack_cnt tinyint DEFAULT 0 NOT NULL,
                                  CONSTRAINT PK__sp_fl_ch__77B55908225C4940 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id,fixturetype_rollup_id),
                                  CONSTRAINT FK_sfcf2 FOREIGN KEY (flow_strategy_code) REFERENCES fp_strategy_text(flow_strategy_code),
                                  CONSTRAINT FK_sfcf3 FOREIGN KEY (fixturetype_rollup_id) REFERENCES fixturetype_rollup(fixturetype_rollup_id),
                                  CONSTRAINT FK_sscf2 FOREIGN KEY (flow_strategy_code) REFERENCES fp_strategy_text(flow_strategy_code)
);


-- us_wm_aex_spo.dbo.sp_style_chan_fixtr definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sp_style_chan_fixtr;

CREATE TABLE sp_style_chan_fixtr (
                                     plan_id bigint NOT NULL,
                                     rpt_lvl_0_nbr int NOT NULL,
                                     rpt_lvl_1_nbr int NOT NULL,
                                     rpt_lvl_2_nbr int NOT NULL,
                                     rpt_lvl_3_nbr int NOT NULL,
                                     rpt_lvl_4_nbr int NOT NULL,
                                     fineline_nbr smallint NOT NULL,
                                     style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                     channel_id tinyint NOT NULL,
                                     fixturetype_rollup_id smallint NOT NULL,
                                     flow_strategy_code tinyint NULL,
                                     merch_method_code smallint NULL,
                                     merch_method_short_desc smallint NULL,
                                     bump_pack_qty bigint NULL,
                                     initial_set_qty bigint NULL,
                                     buy_qty bigint NULL,
                                     repln_qty bigint NULL,
                                     adj_repln_qty bigint NULL,
                                     store_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     CONSTRAINT PK__sp_style__7DC3461A4EEB346F PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id,fixturetype_rollup_id),
                                     CONSTRAINT FK_sscf1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id,fixturetype_rollup_id) REFERENCES sp_fl_chan_fixtr(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id,fixturetype_rollup_id)
);


-- us_wm_aex_spo.dbo.subcatg_plan definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.subcatg_plan;

CREATE TABLE subcatg_plan (
                              plan_id bigint NOT NULL,
                              rpt_lvl_0_nbr int NOT NULL,
                              rpt_lvl_1_nbr int NOT NULL,
                              rpt_lvl_2_nbr int NOT NULL,
                              rpt_lvl_3_nbr int NOT NULL,
                              rpt_lvl_4_nbr int NOT NULL,
                              channel_id tinyint NOT NULL,
                              rpt_lvl_0_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                              rpt_lvl_1_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                              rpt_lvl_2_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                              rpt_lvl_3_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                              rpt_lvl_4_gen_desc1 nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                              CONSTRAINT PK__subcatg___97B87931C5FB3549 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id),
                              CONSTRAINT FK_ps1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id) REFERENCES merchcatg_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.subcatg_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.subcatg_replpk_cons;

CREATE TABLE subcatg_replpk_cons (
                                     plan_id bigint NOT NULL,
                                     rpt_lvl_0_nbr int NOT NULL,
                                     rpt_lvl_1_nbr int NOT NULL,
                                     rpt_lvl_2_nbr int NOT NULL,
                                     rpt_lvl_3_nbr int NOT NULL,
                                     rpt_lvl_4_nbr int NOT NULL,
                                     channel_id tinyint NOT NULL,
                                     final_buy_units bigint NULL,
                                     repl_units bigint NULL,
                                     vendor_pack_cnt bigint NULL,
                                     whse_pack_cnt bigint NULL,
                                     vnpk_whpk_ratio decimal(7,3) NULL,
                                     repl_pack_cnt bigint NULL,
                                     CONSTRAINT PK__subcatg___97B8793125F76E97 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id),
                                     CONSTRAINT FK_src1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id) REFERENCES merchcatg_replpk_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.fineline_plan definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.fineline_plan;

CREATE TABLE fineline_plan (
                               plan_id bigint NOT NULL,
                               rpt_lvl_0_nbr int NOT NULL,
                               rpt_lvl_1_nbr int NOT NULL,
                               rpt_lvl_2_nbr int NOT NULL,
                               rpt_lvl_3_nbr int NOT NULL,
                               rpt_lvl_4_nbr int NOT NULL,
                               fineline_nbr smallint NOT NULL,
                               channel_id tinyint NOT NULL,
                               fineline_desc nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               alt_fineline_desc nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               CONSTRAINT PK__fineline__6B1F0FE26858DDCF PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id),
                               CONSTRAINT FK_pf1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id) REFERENCES subcatg_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.fineline_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.fineline_replpk_cons;

CREATE TABLE fineline_replpk_cons (
                                      plan_id bigint NOT NULL,
                                      rpt_lvl_0_nbr int NOT NULL,
                                      rpt_lvl_1_nbr int NOT NULL,
                                      rpt_lvl_2_nbr int NOT NULL,
                                      rpt_lvl_3_nbr int NOT NULL,
                                      rpt_lvl_4_nbr int NOT NULL,
                                      fineline_nbr smallint NOT NULL,
                                      channel_id tinyint NOT NULL,
                                      final_buy_units bigint NULL,
                                      repl_units bigint NULL,
                                      vendor_pack_cnt bigint NULL,
                                      whse_pack_cnt bigint NULL,
                                      vnpk_whpk_ratio decimal(7,3) NULL,
                                      run_status_code tinyint NOT NULL,
                                      repl_pack_cnt bigint NULL,
                                      CONSTRAINT PK__fineline__6B1F0FE2B63F4867 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id),
                                      CONSTRAINT FK_frc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id) REFERENCES subcatg_replpk_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id),
                                      CONSTRAINT FK_frc2 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code)
);


-- us_wm_aex_spo.dbo.merchcatg_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.merchcatg_pkopt_cons;

CREATE TABLE merchcatg_pkopt_cons (
                                      plan_id bigint NOT NULL,
                                      rpt_lvl_0_nbr int NOT NULL,
                                      rpt_lvl_1_nbr int NOT NULL,
                                      rpt_lvl_2_nbr int NOT NULL,
                                      rpt_lvl_3_nbr int NOT NULL,
                                      channel_id tinyint NOT NULL,
                                      vendor_nbr_6 int NULL,
                                      vendor_nbr_9 int NULL,
                                      vendor_name nvarchar(160) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      origin_country_code char(2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      origin_country_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      port_of_origin_id bigint NULL,
                                      port_of_origin_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      max_units_per_pack bigint NULL,
                                      max_nbr_of_packs bigint NULL,
                                      color_combination varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                      gsm_supplier_id bigint NULL,
                                      select_status_id tinyint NOT NULL,
                                      CONSTRAINT PK__merchcat__531B104FCD2B14E4 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                      CONSTRAINT FK_mpc1 FOREIGN KEY (channel_id) REFERENCES channel_text(channel_id),
                                      CONSTRAINT FK_mpc2 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id) REFERENCES merchcatg_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                      CONSTRAINT FK_mpss1 FOREIGN KEY (select_status_id) REFERENCES select_status_text(select_status_id)
);


-- us_wm_aex_spo.dbo.rc_fl_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_fl_replpk_fixtr_cons;

CREATE TABLE rc_fl_replpk_fixtr_cons (
                                         plan_id bigint NOT NULL,
                                         rpt_lvl_0_nbr int NOT NULL,
                                         rpt_lvl_1_nbr int NOT NULL,
                                         rpt_lvl_2_nbr int NOT NULL,
                                         rpt_lvl_3_nbr int NOT NULL,
                                         rpt_lvl_4_nbr int NOT NULL,
                                         fineline_nbr smallint NOT NULL,
                                         fixturetype_rollup_id smallint NOT NULL,
                                         channel_id tinyint NOT NULL,
                                         final_buy_units bigint NULL,
                                         repl_units bigint NULL,
                                         vendor_pack_cnt bigint NULL,
                                         whse_pack_cnt bigint NULL,
                                         vnpk_whpk_ratio decimal(7,3) NULL,
                                         run_status_code tinyint NOT NULL,
                                         repl_pack_cnt bigint NULL,
                                         fixturetype_rollup_name nvarchar(75) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                         CONSTRAINT PK__rc_fl_re__5C341A4B0C42D237 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,fixturetype_rollup_id,channel_id),
                                         CONSTRAINT FK_rfrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fixturetype_rollup_id,channel_id) REFERENCES rc_subcatg_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fixturetype_rollup_id,channel_id),
                                         CONSTRAINT FK_rfrc2 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code)
);


-- us_wm_aex_spo.dbo.rc_style_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_style_replpk_fixtr_cons;

CREATE TABLE rc_style_replpk_fixtr_cons (
                                            plan_id bigint NOT NULL,
                                            rpt_lvl_0_nbr int NOT NULL,
                                            rpt_lvl_1_nbr int NOT NULL,
                                            rpt_lvl_2_nbr int NOT NULL,
                                            rpt_lvl_3_nbr int NOT NULL,
                                            rpt_lvl_4_nbr int NOT NULL,
                                            fineline_nbr smallint NOT NULL,
                                            style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                            fixturetype_rollup_id smallint NOT NULL,
                                            channel_id tinyint NOT NULL,
                                            final_buy_units bigint NULL,
                                            repl_units bigint NULL,
                                            vendor_pack_cnt bigint NULL,
                                            whse_pack_cnt bigint NULL,
                                            vnpk_whpk_ratio decimal(7,3) NULL,
                                            repl_pack_cnt bigint NULL,
                                            CONSTRAINT PK__rc_style__4F7B522EFF1D1898 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,fixturetype_rollup_id,channel_id),
                                            CONSTRAINT FK_rstrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,fixturetype_rollup_id,channel_id) REFERENCES rc_fl_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,fixturetype_rollup_id,channel_id)
);


-- us_wm_aex_spo.dbo.sp_cc_chan_fixtr definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sp_cc_chan_fixtr;

CREATE TABLE sp_cc_chan_fixtr (
                                  plan_id bigint NOT NULL,
                                  rpt_lvl_0_nbr int NOT NULL,
                                  rpt_lvl_1_nbr int NOT NULL,
                                  rpt_lvl_2_nbr int NOT NULL,
                                  rpt_lvl_3_nbr int NOT NULL,
                                  rpt_lvl_4_nbr int NOT NULL,
                                  fineline_nbr smallint NOT NULL,
                                  style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  channel_id tinyint NOT NULL,
                                  fixturetype_rollup_id smallint NOT NULL,
                                  flow_strategy_code tinyint NULL,
                                  merch_method_code smallint NULL,
                                  merch_method_short_desc smallint NULL,
                                  bump_pack_qty bigint NULL,
                                  initial_set_qty bigint NULL,
                                  buy_qty bigint NULL,
                                  repln_qty bigint NULL,
                                  adj_repln_qty bigint NULL,
                                  store_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  CONSTRAINT PK__sp_cc_ch__5AB4CEA5789CD197 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id,fixturetype_rollup_id),
                                  CONSTRAINT FK_sccf1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id,fixturetype_rollup_id) REFERENCES sp_style_chan_fixtr(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id,fixturetype_rollup_id),
                                  CONSTRAINT FK_sccf2 FOREIGN KEY (flow_strategy_code) REFERENCES fp_strategy_text(flow_strategy_code)
);


-- us_wm_aex_spo.dbo.sp_cc_chan_fixtr_size definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.sp_cc_chan_fixtr_size;

CREATE TABLE sp_cc_chan_fixtr_size (
                                       plan_id bigint NOT NULL,
                                       rpt_lvl_0_nbr int NOT NULL,
                                       rpt_lvl_1_nbr int NOT NULL,
                                       rpt_lvl_2_nbr int NOT NULL,
                                       rpt_lvl_3_nbr int NOT NULL,
                                       rpt_lvl_4_nbr int NOT NULL,
                                       fineline_nbr smallint NOT NULL,
                                       style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                       customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                       channel_id tinyint NOT NULL,
                                       fixturetype_rollup_id smallint NOT NULL,
                                       ahs_size_id bigint NOT NULL,
                                       ahs_size_desc varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                       flow_strategy_code tinyint NULL,
                                       merch_method_code smallint NULL,
                                       merch_method_short_desc smallint NULL,
                                       bump_pack_qty bigint NULL,
                                       initial_set_qty bigint NULL,
                                       buy_qty bigint NULL,
                                       repln_qty bigint NULL,
                                       adj_repln_qty bigint NULL,
                                       store_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                       CONSTRAINT PK__sp_cc_ch__1090993DB570EE82 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id,fixturetype_rollup_id,ahs_size_id),
                                       CONSTRAINT FK_sccfs1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id,fixturetype_rollup_id) REFERENCES sp_cc_chan_fixtr(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id,fixturetype_rollup_id),
                                       CONSTRAINT FK_sccfs2 FOREIGN KEY (flow_strategy_code) REFERENCES fp_strategy_text(flow_strategy_code)
);


-- us_wm_aex_spo.dbo.style_plan definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.style_plan;

CREATE TABLE style_plan (
                            plan_id bigint NOT NULL,
                            rpt_lvl_0_nbr int NOT NULL,
                            rpt_lvl_1_nbr int NOT NULL,
                            rpt_lvl_2_nbr int NOT NULL,
                            rpt_lvl_3_nbr int NOT NULL,
                            rpt_lvl_4_nbr int NOT NULL,
                            fineline_nbr smallint NOT NULL,
                            style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                            channel_id tinyint NOT NULL,
                            CONSTRAINT PK__style_pl__DC09E3742FA25B92 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id),
                            CONSTRAINT FK_sp1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id) REFERENCES fineline_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.style_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.style_replpk_cons;

CREATE TABLE style_replpk_cons (
                                   plan_id bigint NOT NULL,
                                   rpt_lvl_0_nbr int NOT NULL,
                                   rpt_lvl_1_nbr int NOT NULL,
                                   rpt_lvl_2_nbr int NOT NULL,
                                   rpt_lvl_3_nbr int NOT NULL,
                                   rpt_lvl_4_nbr int NOT NULL,
                                   fineline_nbr smallint NOT NULL,
                                   style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                   channel_id tinyint NOT NULL,
                                   final_buy_units bigint NULL,
                                   repl_units bigint NULL,
                                   vendor_pack_cnt bigint NULL,
                                   whse_pack_cnt bigint NULL,
                                   vnpk_whpk_ratio decimal(7,3) NULL,
                                   repl_pack_cnt bigint NULL,
                                   CONSTRAINT PK__style_re__DC09E374374FB8A8 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id),
                                   CONSTRAINT FK_strc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id) REFERENCES fineline_replpk_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.subcatg_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.subcatg_pkopt_cons;

CREATE TABLE subcatg_pkopt_cons (
                                    plan_id bigint NOT NULL,
                                    rpt_lvl_0_nbr int NOT NULL,
                                    rpt_lvl_1_nbr int NOT NULL,
                                    rpt_lvl_2_nbr int NOT NULL,
                                    rpt_lvl_3_nbr int NOT NULL,
                                    rpt_lvl_4_nbr int NOT NULL,
                                    channel_id tinyint NOT NULL,
                                    vendor_nbr_6 int NULL,
                                    vendor_nbr_9 int NULL,
                                    vendor_name nvarchar(160) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    origin_country_code char(2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    origin_country_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    port_of_origin_id bigint NULL,
                                    port_of_origin_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    max_units_per_pack bigint NULL,
                                    max_nbr_of_packs bigint NULL,
                                    color_combination varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                    gsm_supplier_id bigint NULL,
                                    select_status_id tinyint NOT NULL,
                                    CONSTRAINT PK__subcatg___97B87931E481EB91 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id),
                                    CONSTRAINT FK_sp3 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id) REFERENCES merchcatg_pkopt_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,channel_id),
                                    CONSTRAINT FK_spss1 FOREIGN KEY (select_status_id) REFERENCES select_status_text(select_status_id)
);


-- us_wm_aex_spo.dbo.cc_plan definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.cc_plan;

CREATE TABLE cc_plan (
                         plan_id bigint NOT NULL,
                         rpt_lvl_0_nbr int NOT NULL,
                         rpt_lvl_1_nbr int NOT NULL,
                         rpt_lvl_2_nbr int NOT NULL,
                         rpt_lvl_3_nbr int NOT NULL,
                         rpt_lvl_4_nbr int NOT NULL,
                         fineline_nbr smallint NOT NULL,
                         style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                         customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                         channel_id tinyint NOT NULL,
                         color_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                         color_family_desc nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                         CONSTRAINT PK__cc_plan__B0A864F301039955 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id),
                         CONSTRAINT FK_cp1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id) REFERENCES style_plan(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.cc_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.cc_replpk_cons;

CREATE TABLE cc_replpk_cons (
                                plan_id bigint NOT NULL,
                                rpt_lvl_0_nbr int NOT NULL,
                                rpt_lvl_1_nbr int NOT NULL,
                                rpt_lvl_2_nbr int NOT NULL,
                                rpt_lvl_3_nbr int NOT NULL,
                                rpt_lvl_4_nbr int NOT NULL,
                                fineline_nbr smallint NOT NULL,
                                style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                channel_id tinyint NOT NULL,
                                color_family_desc nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                final_buy_units bigint NULL,
                                repl_units bigint NULL,
                                vendor_pack_cnt bigint NULL,
                                whse_pack_cnt bigint NULL,
                                vnpk_whpk_ratio decimal(7,3) NULL,
                                repl_pack_cnt bigint NULL,
                                CONSTRAINT PK__cc_replp__B0A864F39CD18639 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id),
                                CONSTRAINT FK_crc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id) REFERENCES style_replpk_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id)
);


-- us_wm_aex_spo.dbo.cc_sp_replpk_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.cc_sp_replpk_cons;

CREATE TABLE cc_sp_replpk_cons (
                                   plan_id bigint NOT NULL,
                                   rpt_lvl_0_nbr int NOT NULL,
                                   rpt_lvl_1_nbr int NOT NULL,
                                   rpt_lvl_2_nbr int NOT NULL,
                                   rpt_lvl_3_nbr int NOT NULL,
                                   rpt_lvl_4_nbr int NOT NULL,
                                   fineline_nbr smallint NOT NULL,
                                   style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                   customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                   ahs_size_id bigint NOT NULL,
                                   channel_id tinyint NOT NULL,
                                   size_desc nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   color_family_desc nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   final_buy_units bigint NULL,
                                   repl_units bigint NULL,
                                   vendor_pack_cnt bigint NULL,
                                   whse_pack_cnt bigint NULL,
                                   vnpk_whpk_ratio decimal(7,3) NULL,
                                   repl_pack_cnt bigint NULL,
                                   merch_method_code smallint NULL,
                                   merch_method_short_desc nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   replen_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                   CONSTRAINT PK__cc_sp_re__9C0272C285A7BF71 PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,ahs_size_id,channel_id),
                                   CONSTRAINT FK_cssrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id) REFERENCES cc_replpk_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id)
);


-- us_wm_aex_spo.dbo.fineline_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.fineline_pkopt_cons;

CREATE TABLE fineline_pkopt_cons (
                                     plan_id bigint NOT NULL,
                                     rpt_lvl_0_nbr int NOT NULL,
                                     rpt_lvl_1_nbr int NOT NULL,
                                     rpt_lvl_2_nbr int NOT NULL,
                                     rpt_lvl_3_nbr int NOT NULL,
                                     rpt_lvl_4_nbr int NOT NULL,
                                     fineline_nbr smallint NOT NULL,
                                     channel_id tinyint NOT NULL,
                                     vendor_nbr_6 int NULL,
                                     vendor_nbr_9 int NULL,
                                     vendor_name nvarchar(160) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     origin_country_code char(2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     origin_country_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     port_of_origin_id bigint NULL,
                                     port_of_origin_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     max_units_per_pack bigint NULL,
                                     max_nbr_of_packs bigint NULL,
                                     color_combination varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                     run_status_code tinyint NOT NULL,
                                     gsm_supplier_id bigint NULL,
                                     select_status_id tinyint NOT NULL,
                                     CONSTRAINT PK__fineline__6B1F0FE2400FC80A PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id),
                                     CONSTRAINT FK_fpc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id) REFERENCES subcatg_pkopt_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,channel_id),
                                     CONSTRAINT FK_fpc3 FOREIGN KEY (run_status_code) REFERENCES run_status_text(run_status_code),
                                     CONSTRAINT FK_fpss1 FOREIGN KEY (select_status_id) REFERENCES select_status_text(select_status_id)
);


-- us_wm_aex_spo.dbo.rc_cc_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_cc_replpk_fixtr_cons;

CREATE TABLE rc_cc_replpk_fixtr_cons (
                                         plan_id bigint NOT NULL,
                                         rpt_lvl_0_nbr int NOT NULL,
                                         rpt_lvl_1_nbr int NOT NULL,
                                         rpt_lvl_2_nbr int NOT NULL,
                                         rpt_lvl_3_nbr int NOT NULL,
                                         rpt_lvl_4_nbr int NOT NULL,
                                         fineline_nbr smallint NOT NULL,
                                         style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                         customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                         fixturetype_rollup_id smallint NOT NULL,
                                         channel_id tinyint NOT NULL,
                                         color_family_desc nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                         final_buy_units bigint NULL,
                                         repl_units bigint NULL,
                                         vendor_pack_cnt bigint NULL,
                                         whse_pack_cnt bigint NULL,
                                         vnpk_whpk_ratio decimal(7,3) NULL,
                                         repl_pack_cnt bigint NULL,
                                         CONSTRAINT PK__rc_cc_re__199F4FE60E2EAE2C PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id),
                                         CONSTRAINT FK_rcrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,fixturetype_rollup_id,channel_id) REFERENCES rc_style_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,fixturetype_rollup_id,channel_id)
);


-- us_wm_aex_spo.dbo.style_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.style_pkopt_cons;

CREATE TABLE style_pkopt_cons (
                                  plan_id bigint NOT NULL,
                                  rpt_lvl_0_nbr int NOT NULL,
                                  rpt_lvl_1_nbr int NOT NULL,
                                  rpt_lvl_2_nbr int NOT NULL,
                                  rpt_lvl_3_nbr int NOT NULL,
                                  rpt_lvl_4_nbr int NOT NULL,
                                  fineline_nbr smallint NOT NULL,
                                  style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                  channel_id tinyint NOT NULL,
                                  vendor_nbr_6 int NULL,
                                  vendor_nbr_9 int NULL,
                                  vendor_name nvarchar(160) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  origin_country_code char(2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  origin_country_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  port_of_origin_id bigint NULL,
                                  port_of_origin_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  max_units_per_pack bigint NULL,
                                  max_nbr_of_packs bigint NULL,
                                  color_combination varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                  gsm_supplier_id bigint NULL,
                                  select_status_id tinyint NOT NULL,
                                  CONSTRAINT PK__style_pk__DC09E374B0DA77FF PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id),
                                  CONSTRAINT FK_sspc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id) REFERENCES fineline_pkopt_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,channel_id),
                                  CONSTRAINT FK_stpss1 FOREIGN KEY (select_status_id) REFERENCES select_status_text(select_status_id)
);


-- us_wm_aex_spo.dbo.cc_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.cc_pkopt_cons;

-- us_wm_aex_spo.dbo.cc_pkopt_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.cc_pkopt_cons;

CREATE TABLE cc_pkopt_cons (
                               plan_id bigint NOT NULL,
                               rpt_lvl_0_nbr int NOT NULL,
                               rpt_lvl_1_nbr int NOT NULL,
                               rpt_lvl_2_nbr int NOT NULL,
                               rpt_lvl_3_nbr int NOT NULL,
                               rpt_lvl_4_nbr int NOT NULL,
                               fineline_nbr smallint NOT NULL,
                               style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                               customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                               channel_id tinyint NOT NULL,
                               vendor_nbr_6 int NULL,
                               vendor_nbr_9 int NULL,
                               vendor_name nvarchar(160) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               origin_country_code char(2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               origin_country_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               port_of_origin_id bigint NULL,
                               port_of_origin_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               max_units_per_pack bigint NULL,
                               max_nbr_of_packs bigint NULL,
                               color_combination varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               select_status_id tinyint DEFAULT 0 NOT NULL,
                               gsm_supplier_id bigint NULL,
                               override_factory_id nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               override_factory_name nvarchar(80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                               CONSTRAINT PK__cc_pkopt__B0A864F3CE7891DB PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,channel_id),
                               CONSTRAINT FK_cpc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id) REFERENCES style_pkopt_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,channel_id),
                               CONSTRAINT FK_cpss1 FOREIGN KEY (select_status_id) REFERENCES select_status_text(select_status_id)
);

-- us_wm_aex_spo.dbo.rc_cc_mm_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_cc_mm_replpk_fixtr_cons;

CREATE TABLE rc_cc_mm_replpk_fixtr_cons (
                                            plan_id bigint NOT NULL,
                                            rpt_lvl_0_nbr int NOT NULL,
                                            rpt_lvl_1_nbr int NOT NULL,
                                            rpt_lvl_2_nbr int NOT NULL,
                                            rpt_lvl_3_nbr int NOT NULL,
                                            rpt_lvl_4_nbr int NOT NULL,
                                            fineline_nbr smallint NOT NULL,
                                            style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                            customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                            fixturetype_rollup_id smallint NOT NULL,
                                            channel_id tinyint NOT NULL,
                                            merch_method_code smallint NOT NULL,
                                            color_family_desc nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                            final_buy_units bigint NULL,
                                            repl_units bigint NULL,
                                            vendor_pack_cnt bigint NULL,
                                            whse_pack_cnt bigint NULL,
                                            vnpk_whpk_ratio decimal(7,3) NULL,
                                            repl_pack_cnt bigint NULL,
                                            merch_method_short_desc nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                            replen_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                            CONSTRAINT PK__rc_cc_mm__3CCA23247F5865FD PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id,merch_method_code),
                                            CONSTRAINT FK_rcmrfc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id) REFERENCES rc_cc_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id)
);


-- us_wm_aex_spo.dbo.rc_cc_sp_mm_replpk_fixtr_cons definition

-- Drop table

-- DROP TABLE us_wm_aex_spo.dbo.rc_cc_sp_mm_replpk_fixtr_cons;

CREATE TABLE rc_cc_sp_mm_replpk_fixtr_cons (
                                               plan_id bigint NOT NULL,
                                               rpt_lvl_0_nbr int NOT NULL,
                                               rpt_lvl_1_nbr int NOT NULL,
                                               rpt_lvl_2_nbr int NOT NULL,
                                               rpt_lvl_3_nbr int NOT NULL,
                                               rpt_lvl_4_nbr int NOT NULL,
                                               fineline_nbr smallint NOT NULL,
                                               style_nbr char(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                               customer_choice char(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
                                               ahs_size_id bigint NOT NULL,
                                               fixturetype_rollup_id smallint NOT NULL,
                                               channel_id tinyint NOT NULL,
                                               merch_method_code smallint NOT NULL,
                                               size_desc nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                               color_family_desc nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                               final_buy_units bigint NULL,
                                               repl_units bigint NULL,
                                               vendor_pack_cnt bigint NULL,
                                               whse_pack_cnt bigint NULL,
                                               vnpk_whpk_ratio decimal(7,3) NULL,
                                               repl_pack_cnt bigint NULL,
                                               merch_method_short_desc nvarchar(40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                               replen_obj nvarchar(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                               CONSTRAINT PK__rc_cc_sp__E4C456BFC9971E0E PRIMARY KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,ahs_size_id,fixturetype_rollup_id,channel_id,merch_method_code),
                                               CONSTRAINT FK_rmcssrc1 FOREIGN KEY (plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id,merch_method_code) REFERENCES rc_cc_mm_replpk_fixtr_cons(plan_id,rpt_lvl_0_nbr,rpt_lvl_1_nbr,rpt_lvl_2_nbr,rpt_lvl_3_nbr,rpt_lvl_4_nbr,fineline_nbr,style_nbr,customer_choice,fixturetype_rollup_id,channel_id,merch_method_code)
);

-- Static data
----
INSERT INTO channel_text (channel_id, channel_desc) VALUES(1, N'Store');
INSERT INTO channel_text (channel_id, channel_desc) VALUES(2, N'Online');
INSERT INTO channel_text (channel_id, channel_desc) VALUES(3, N'Omni');
----
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(1, N'INITIAL SET');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(2, N'INITIAL + BUMP SET');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(3, N'INITIAL + REPLENISHMENT');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(4, N'BUMP_REPLINISHMENT');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(5, N'INITIAL_BUMP');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(6, N'INITIAL_REPLENISHMENT');
INSERT INTO fp_strategy_text (flow_strategy_code, flow_strategy_desc) VALUES(7, N'INITIAL_BUMP_REPLENISHMENT');
----
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(-1, N'DEFAULT', N'Default value');
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(0, N'ONLINE', N'Used for online where there are no fixtures');
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(1, N'WALLS', N'Gondola and T SYSTEM');
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(2, N'ENDCAPS', N'ENDCAP is an offical fixture type');
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(3, N'RACKS', N'CIRCLE RACK, H RACK, 4 WAY, Apparel Rack fixtures');
INSERT INTO fixturetype_rollup (fixturetype_rollup_id, fixturetype_rollup_name, fixturetype_rollup_desc) VALUES(4, N'TABLES', N'TABLE is an official fixture type');
----
INSERT INTO select_status_text (select_status_id, select_status_desc) VALUES(0, N'Not Selected');
INSERT INTO select_status_text (select_status_id, select_status_desc) VALUES(1, N'Selected');
INSERT INTO select_status_text (select_status_id, select_status_desc) VALUES(2, N'Partially Selected');
----
INSERT INTO run_status_text (run_status_code, run_status_desc) VALUES(0, N'NOT SENT');
INSERT INTO run_status_text (run_status_code, run_status_desc) VALUES(3, N'SENT');
INSERT INTO run_status_text (run_status_code, run_status_desc) VALUES(6, N'COMPLETED');
INSERT INTO run_status_text (run_status_code, run_status_desc) VALUES(10, N'ERROR');
----
