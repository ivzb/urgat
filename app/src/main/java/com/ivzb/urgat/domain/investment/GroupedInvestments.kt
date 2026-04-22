package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.ui.components.GroupedItems

typealias GroupedInvestments = GroupedItems<String, List<InvestmentTotal>, Investment>
